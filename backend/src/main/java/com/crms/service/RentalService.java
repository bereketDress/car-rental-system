package com.crms.service;
import com.crms.model.*;
import lombok.RequiredArgsConstructor;
import com.crms.model.Car;
import com.crms.model.Rental;
import com.crms.model.Reservation;
import com.crms.repository.CarRepository;
import com.crms.repository.DamageRepository;
import com.crms.repository.RentalRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.crms.util.EntityFields.*;


@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ReservationService reservationService;
    private final CarRepository carRepository;
    private final DamageRepository damageRepository;

    public List<Rental> getAll() { return withCharges(rentalRepository.findAll()); }

    public Rental getRental(Long id) {
        return withCharges(rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + id)));
    }

    public Rental checkOut(Long reservationId, Integer startMileage, LocalDate returnDate) {
        return checkOut(reservationId, startMileage, returnDate, null);
    }

    public Rental checkOut(Long reservationId, Integer startMileage, LocalDate returnDate, Long customerId) {
        Rental existingRental = rentalRepository.findByReservationReservationId(reservationId).orElse(null);
        if (existingRental != null) {
            return withCharges(existingRental);
        }

        Reservation reservation = reservationService.getByIdWithDetails(reservationId);

        Customer reservationCustomer = get(reservation, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(reservationCustomer, "customerId"))) {
            throw new RuntimeException("Customers can only check out their own reservations.");
        }

        if (RESERVATION_PENDING.equalsIgnoreCase(string(reservation, "status"))) {
            reservation = reservationService.confirmReservation(longValue(reservation, "reservationId"));
        }

        if (!RESERVATION_CONFIRMED.equalsIgnoreCase(string(reservation, "status"))) {
            throw new RuntimeException("Only PENDING or CONFIRMED reservations can be checked out.");
        }

        Car car = reservationCar(reservation);
        if (car == null) throw new RuntimeException("No car assigned to reservation.");

        setAvailable(car, false);
        carRepository.save(car);

        reservation = reservationService.convertReservation(longValue(reservation, "reservationId"));

        Rental rental = new Rental();
        set(rental, "checkoutDate", LocalDate.now());
        set(rental, "status", RENTAL_ACTIVE);
        set(rental, "returnDate", returnDate);
        set(rental, "startMileage", startMileage);
        set(rental, "reservation", reservation);
        set(rental, "customer", reservationCustomer);
        set(rental, "car", car);

        return withCharges(rentalRepository.save(rental));
    }

    public Rental checkIn(Long rentalId, Integer endMileage, String damageDescription, Float repairCost) {
        return checkIn(rentalId, endMileage, damageDescription, repairCost, null);
    }

    public Rental checkIn(Long rentalId, Integer endMileage, String damageDescription, Float repairCost, Long customerId) {
        Rental rental = getRental(rentalId);

        Customer rentalCustomer = get(rental, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(rentalCustomer, "customerId"))) {
            throw new RuntimeException("Customers can only check in their own rentals.");
        }

        if (!RENTAL_ACTIVE.equalsIgnoreCase(string(rental, "status"))) {
            throw new RuntimeException("Only active rentals can be checked in.");
        }

        set(rental, "endMileage", endMileage);
        
        LocalDate actualReturnDate = LocalDate.now();
        LocalDate returnDate = date(rental, "returnDate");
        if (returnDate != null && actualReturnDate.isAfter(returnDate)) {
            long overdueDays = ChronoUnit.DAYS.between(returnDate, actualReturnDate);
            Car rentalCar = get(rental, "car", Car.class);
            Double dailyRate = rentalCar == null ? 0.0 : doubleValue(rentalCar, "dailyRate");
            Customer customer = get(rental, "customer", Customer.class);
            if (customer != null) {
                Double currentBalance = doubleValue(customer, "outstandingBalance");
                set(customer, "outstandingBalance", (currentBalance == null ? 0.0 : currentBalance) + overdueDays * dailyRate);
            }
        }
        
        set(rental, "status", RENTAL_RETURNED);

        Car car = get(rental, "car", Car.class);
        if (car != null) {
            set(car, "mileage", endMileage);
            carRepository.save(car);
        }

        Rental saved = rentalRepository.save(rental);

        if (damageDescription != null && !damageDescription.isBlank()) {
            Damage damage = new Damage();
            set(damage, "reportDate", LocalDate.now());
            set(damage, "description", damageDescription);
            set(damage, "repairCost", repairCost != null ? repairCost.doubleValue() : 0.0);
            set(damage, "status", DAMAGE_REPORTED);
            set(damage, "rental", saved);
            damageRepository.save(damage);
        }

        return withCharges(saved);
    }

    public List<Rental> listActive() {
        return withCharges(rentalRepository.findByStatus(RENTAL_ACTIVE));
    }

    public List<Rental> listOverdue() {
        return withCharges(rentalRepository.findByStatus(RENTAL_ACTIVE).stream()
                .filter(r -> {
                    LocalDate returnDate = date(r, "returnDate");
                    return returnDate != null && LocalDate.now().isAfter(returnDate);
                })
                .toList());
    }

    public List<Rental> listByCustomer(Long custId) {
        return withCharges(rentalRepository.findByCustomerCustomerId(custId));
    }

    public void delete(Long id) { rentalRepository.deleteById(id); }

    private List<Rental> withCharges(List<Rental> rentals) {
        return rentals.stream().map(this::withCharges).toList();
    }

    private Rental withCharges(Rental rental) {
        if (rental == null) {
            return null;
        }

        Long rentalId = longValue(rental, "rentalId");
        if (rentalId != null) {
            set(rental, "damages", damageRepository.findByRentalRentalId(rentalId));
        }

        set(rental, "baseCharge", baseRentalCharge(rental));
        set(rental, "damageRepairCost", damageRepairCost(rental));
        set(rental, "totalCharge", rentalCharge(rental));
        return rental;
    }
}
