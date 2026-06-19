package com.crms.service;

import com.crms.dto.reservation.ReservationResponse;
import com.crms.model.Car;
import com.crms.model.Customer;
import com.crms.model.Reservation;
import com.crms.repository.CarRepository;
import com.crms.repository.CustomerRepository;
import com.crms.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final CustomerService customerService;
    private final CarService carService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> listAll() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse createReservation(Long customerId, Long carId, LocalDate pickupDate) {

        if (!customerService.eligibleForReservation(customerId)) {
            throw new RuntimeException("Customer has outstanding balance.");
        }

        Car car = carService.getCarEntity(carId);
        if (!"AVAILABLE".equalsIgnoreCase(car.getAvailability())) {
            throw new RuntimeException("Car not available.");
        }

        Reservation reservation = new Reservation();
        reservation.setCars(List.of(car));
        reservation.setPickupDate(pickupDate);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus("PENDING");

        Reservation saved = reservationRepository.save(reservation);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        if (customer.getReservations() == null) customer.setReservations(new ArrayList<>());
        customer.getReservations().add(saved);
        customerRepository.save(customer);

        if (car.getReservations() == null) car.setReservations(new ArrayList<>());
        car.getReservations().add(saved);
        carRepository.save(car);

        carService.updateAvailability(carId, false);

        return toResponse(saved);
    }

    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));

        reservation.setStatus("CONFIRMED");
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));

        reservation.setStatus("CANCELLED");
        Reservation saved = reservationRepository.save(reservation);

        Car car = reservation.getCars().get(0);
        carService.updateAvailability(car.getCarId(), true);

        return toResponse(saved);
    }

    public List<ReservationResponse> listByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        return (customer.getReservations() == null ? List.<Reservation>of() : customer.getReservations())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean customerOwnsReservation(Long reservationId, Long customerId) {
        Customer customer = customerForReservation(reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId)));

        return customer != null && customerId.equals(customer.getCustomerId());
    }

    private ReservationResponse toResponse(Reservation reservation) {
        Customer customer = customerForReservation(reservation);
        Car car = carForReservation(reservation);

        ReservationResponse.CustomerSummary customerSummary = customer == null ? null :
                new ReservationResponse.CustomerSummary(
                        customer.getCustomerId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone()
                );

        ReservationResponse.CarSummary carSummary = car == null ? null :
                new ReservationResponse.CarSummary(
                        car.getCarId(),
                        car.getPlateNumber(),
                        car.getBrand(),
                        car.getModel(),
                        car.getYear() == null ? 0 : car.getYear(),
                        car.getDailyRate() == null ? 0.0 : car.getDailyRate(),
                        car.getCarType()
                );

        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getReservationDate(),
                reservation.getPickupDate(),
                reservation.getStatus(),
                customerSummary,
                null,
                carSummary
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

    private Car carForReservation(Reservation reservation) {
        if (reservation == null || reservation.getCars() == null || reservation.getCars().isEmpty()) {
            return null;
        }

        return reservation.getCars().get(0);
    }
}
