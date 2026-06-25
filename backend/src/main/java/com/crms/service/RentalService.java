package com.crms.service;

import com.crms.dto.rental.check.CheckInRequest;
import com.crms.dto.rental.check.CheckOutRequest;
import com.crms.dto.rental.RentalResponse;
import com.crms.model.*;
import com.crms.repository.CarRepository;
import com.crms.repository.CustomerRepository;
import com.crms.repository.DamageRepository;
import com.crms.repository.RentalRepository;
import com.crms.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final DamageRepository damageRepository;

    public List<RentalResponse> getAll() {
        return rentalRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RentalResponse getRental(Long id) {
        return toResponse(getRentalEntity(id));
    }

    Rental getRentalEntity(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + id));
    }

    public RentalResponse checkOut(CheckOutRequest request, Long customerId) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Customer customer = customerForReservation(reservation);
        if (customerId != null && (customer == null || !customerId.equals(customer.getCustomerId()))) {
            throw new RuntimeException("Customers can only check out their own reservations.");
        }

        Car car = carForReservation(reservation);
        if (car == null) throw new RuntimeException("No car assigned to reservation.");

        car.setAvailability("UNAVAILABLE");
        carRepository.save(car);

        Rental rental = new Rental();
        rental.setCheckoutDate(LocalDate.now());
        rental.setReturnDate(request.returnDate());
        rental.setStartMileage(request.startMileage());
        rental.setStatus("ACTIVE");

        Rental saved = rentalRepository.save(rental);
        reservation.setRental(saved);
        reservation.setStatus("RENTED");
        reservationRepository.save(reservation);

        if (customer != null) {
            if (customer.getRentals() == null) customer.setRentals(new ArrayList<>());
            customer.getRentals().add(saved);
            customerRepository.save(customer);
        }

        if (car.getRentals() == null) car.setRentals(new ArrayList<>());
        car.getRentals().add(saved);
        carRepository.save(car);

        return toResponse(saved);
    }

    public RentalResponse checkIn(CheckInRequest request, Long customerId) {
        Rental rental = getRentalEntity(request.rentalId());
        Customer customer = customerForRental(rental);

        if (customerId != null && (customer == null || !customerId.equals(customer.getCustomerId()))) {
            throw new RuntimeException("Customers can only check in their own rentals.");
        }

        rental.setEndMileage(request.endMileage());
        rental.setStatus("RETURNED");

        Car car = carForRental(rental);
        if (car != null) {
            car.setMileage(request.endMileage());
            car.setAvailability("AVAILABLE");
            carRepository.save(car);
        }

        if (request.damageDescription() != null && !request.damageDescription().isBlank()) {
            Damage damage = new Damage();
            damage.setReportDate(LocalDate.now());
            damage.setDescription(request.damageDescription());
            damage.setRepairCost(request.repairCost() == null ? 0.0F : request.repairCost());
            damage.setStatus("REPORTED");

            Damage savedDamage = damageRepository.save(damage);
            if (rental.getDamage() == null) rental.setDamage(new ArrayList<>());
            rental.getDamage().add(savedDamage);
        }

        return toResponse(rentalRepository.save(rental));
    }

    public List<RentalResponse> listByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        return (customer.getRentals() == null ? List.<Rental>of() : customer.getRentals())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean delete(Long id) {
        rentalRepository.deleteById(id);
        return true;
    }

    float rentalCharge(Rental rental) {
        return baseCharge(rental) + damageCharge(rental);
    }

    private float baseCharge(Rental rental) {
        Car car = carForRental(rental);
        if (car == null || rental.getCheckoutDate() == null || rental.getReturnDate() == null || car.getDailyRate() == null) {
            return 0.0F;
        }

        long days = Math.max(1, ChronoUnit.DAYS.between(rental.getCheckoutDate(), rental.getReturnDate()));
        return (float) (days * car.getDailyRate());
    }

    private float damageCharge(Rental rental) {
        if (rental.getDamage() == null) return 0.0F;

        return (float) rental.getDamage().stream()
                .map(Damage::getRepairCost)
                .filter(cost -> cost != null && cost > 0)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    private RentalResponse toResponse(Rental rental) {
        Car car = carForRental(rental);
        Customer customer = customerForRental(rental);

        RentalResponse.CarSummary carSummary = car == null ? null :
                new RentalResponse.CarSummary(car.getCarId(), car.getBrand(), car.getModel(),
                        car.getPlateNumber(), car.getCarType());

        RentalResponse.CustomerSummary customerSummary = customer == null ? null :
                new RentalResponse.CustomerSummary(customer.getCustomerId(), customer.getName());

        return new RentalResponse(
                rental.getRentalId(),
                rental.getCheckoutDate(),
                rental.getReturnDate(),
                rental.getStartMileage(),
                rental.getEndMileage(),
                rental.getStatus(),
                baseCharge(rental),
                damageCharge(rental),
                rentalCharge(rental),
                carSummary,
                customerSummary
        );
    }

    private Customer customerForReservation(Reservation reservation) {
        if (reservation == null || reservation.getReservationId() == null) return null;

        return customerRepository.findAll().stream()
                .filter(customer -> customer.getReservations() != null)
                .filter(customer -> customer.getReservations().stream()
                        .anyMatch(r -> reservation.getReservationId().equals(r.getReservationId())))
                .findFirst()
                .orElse(null);
    }

    private Customer customerForRental(Rental rental) {
        if (rental == null || rental.getRentalId() == null) return null;

        return customerRepository.findAll().stream()
                .filter(customer -> customer.getRentals() != null)
                .filter(customer -> customer.getRentals().stream()
                        .anyMatch(r -> rental.getRentalId().equals(r.getRentalId())))
                .findFirst()
                .orElse(null);
    }

    private Car carForReservation(Reservation reservation) {
        if (reservation == null || reservation.getCars() == null || reservation.getCars().isEmpty()) return null;
        return reservation.getCars().get(0);
    }

    private Car carForRental(Rental rental) {
        if (rental == null || rental.getRentalId() == null) return null;

        return carRepository.findAll().stream()
                .filter(car -> car.getRentals() != null)
                .filter(car -> car.getRentals().stream()
                        .anyMatch(r -> rental.getRentalId().equals(r.getRentalId())))
                .findFirst()
                .orElse(null);
    }
}
