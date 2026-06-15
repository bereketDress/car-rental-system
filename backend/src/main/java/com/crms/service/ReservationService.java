package com.crms.service;
import com.crms.dto.reservationDto.ReservationResponse;
import com.crms.exception.CarNotAvailableException;
import com.crms.model.Car;
import com.crms.model.Customer;
import com.crms.model.Reservation;
import com.crms.model.Staff;
import com.crms.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerService customerService;
    private final CarService carService;

    public List<Reservation> getAll() { return reservationRepository.findAll(); }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllResponses() {
        return reservationRepository.findAllWithDetails().stream().map(this::toResponse).toList();
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
    }

    @Transactional(readOnly = true)
    public Reservation getByIdWithDetails(Long id) {
        return reservationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
    }

    @Transactional
    public Reservation createReservation(Long custId, String vinNumber, LocalDate pickupDate) {
        if (!customerService.eligibleForReservation(custId)) {
            throw new RuntimeException("Customer has an outstanding balance and cannot make a new reservation.");
        }

        if (vinNumber == null || vinNumber.isBlank()) {
            throw new IllegalArgumentException("vinNumber is required");
        }

        Car car = carService.getCar(vinNumber);
        if (!available(car)) {
            throw new CarNotAvailableException("Car is already reserved or rented.");
        }
        
        Reservation reservation = new Reservation();
        set(reservation, "customer", customerService.getCustomer(custId));
        setReservationCar(reservation, car);
        set(reservation, "pickupDate", pickupDate);
        set(reservation, "reservationDate", LocalDate.now());
        set(reservation, "status", RESERVATION_PENDING);

        Reservation saved = reservationRepository.save(reservation);
        carService.updateAvailability(string(car, "vinNumber"), false);
        return saved;
    }

    @Transactional
    public ReservationResponse createReservationResponse(Long custId, String vinNumber, LocalDate pickupDate) {
        return toResponse(createReservation(custId, vinNumber, pickupDate));
    }

    @Transactional
    public Reservation confirmReservation(Long id) {
        Reservation reservation = getById(id);
        if (!RESERVATION_PENDING.equalsIgnoreCase(string(reservation, "status"))) {
            throw new RuntimeException("Only PENDING reservations can be confirmed.");
        }
        set(reservation, "status", RESERVATION_CONFIRMED);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public ReservationResponse confirmReservationResponse(Long id) {
        confirmReservation(id);
        return toResponse(reservationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id)));
    }

    @Transactional
    public Reservation convertReservation(Long id) {
        Reservation reservation = getById(id);
        if (!RESERVATION_CONFIRMED.equalsIgnoreCase(string(reservation, "status"))) {
            throw new RuntimeException("Only CONFIRMED reservations can be converted to rentals.");
        }
        set(reservation, "status", RESERVATION_CONVERTED);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getByIdWithDetails(id);
        if (RESERVATION_CANCELLED.equalsIgnoreCase(string(reservation, "status"))) {
            throw new RuntimeException("Reservation is already cancelled.");
        }

        Car car = reservationCar(reservation);
        boolean releaseCar = !RESERVATION_CONVERTED.equalsIgnoreCase(string(reservation, "status"))
                && car != null;

        set(reservation, "status", RESERVATION_CANCELLED);
        Reservation saved = reservationRepository.save(reservation);

        if (releaseCar) {
            carService.updateAvailability(string(car, "vinNumber"), true);
        }

        return saved;
    }

    @Transactional
    public ReservationResponse cancelReservationResponse(Long id) {
        cancelReservation(id);
        return toResponse(reservationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id)));
    }

    public List<Reservation> listByCustomer(Long custId) {
        return reservationRepository.findByCustomerCustomerId(custId);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> listResponsesByCustomer(Long custId) {
        return reservationRepository.findByCustomerCustomerIdWithDetails(custId).stream().map(this::toResponse).toList();
    }

    public List<Reservation> listByStatus(String status) {
        return reservationRepository.findByStatus(status);
    }

    public void delete(Long id) { reservationRepository.deleteById(id); }

    public ReservationResponse toResponse(Reservation reservation) {
        Customer customer = get(reservation, "customer", Customer.class);
        Staff staff = get(reservation, "staff", Staff.class);
        Car car = reservationCar(reservation);

        ReservationResponse.CustomerSummary customerSummary = customer == null ? null :
                new ReservationResponse.CustomerSummary(
                        longValue(customer, "customerId"),
                        string(customer, "name"),
                        null,
                        string(customer, "phone")
                );

        ReservationResponse.StaffSummary staffSummary = staff == null ? null :
                new ReservationResponse.StaffSummary(
                        longValue(staff, "staffId"),
                        string(staff, "name"),
                        string(staff, "email"),
                        string(staff, "role")
                );

        ReservationResponse.CarSummary carSummary = car == null ? null :
                new ReservationResponse.CarSummary(
                        string(car, "vinNumber"),
                        string(car, "plateNumber"),
                        string(car, "brand"),
                        string(car, "model"),
                        integer(car, "year") == null ? 0 : integer(car, "year"),
                        doubleValue(car, "dailyRate") == null ? 0.0 : doubleValue(car, "dailyRate"),
                        string(car, "carType")
                );

        return new ReservationResponse(
                longValue(reservation, "reservationId"),
                date(reservation, "reservationDate"),
                date(reservation, "pickupDate"),
                string(reservation, "status"),
                customerSummary,
                staffSummary,
                carSummary
        );
    }

}
