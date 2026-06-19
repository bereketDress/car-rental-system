package com.crms.service;

import com.crms.dto.payment.PaymentRequest;
import com.crms.dto.payment.PaymentResponse;
import com.crms.model.Car;
import com.crms.model.Customer;
import com.crms.model.Payment;
import com.crms.model.Rental;
import com.crms.repository.CarRepository;
import com.crms.repository.CustomerRepository;
import com.crms.repository.PaymentRepository;
import com.crms.repository.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final RentalService rentalService;

    public List<PaymentResponse> getAll() {
        return paymentRepo.findAll().stream().map(this::toResponse).toList();
    }

    public List<PaymentResponse> listByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        return (customer.getRentals() == null ? List.<Rental>of() : customer.getRentals())
                .stream()
                .map(Rental::getPayment)
                .filter(Objects::nonNull)
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse get(Long id) {
        return toResponse(paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found")));
    }

    public PaymentResponse computeCharges(Long rentalId, Long customerId) {
        Rental rental = getRental(rentalId, customerId);
        return new PaymentResponse(null, null, rentalService.rentalCharge(rental), null, null, null, rentalId);
    }

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request, Long customerId) {
        Rental rental = getReturnedRental(request.rentalId(), customerId);

        Payment payment = rental.getPayment() == null ? new Payment() : rental.getPayment();
        payment.setPaymentDate(LocalDate.now());
        payment.setAmount(rentalService.rentalCharge(rental));
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus("COMPLETED");

        Payment saved = paymentRepo.save(payment);
        rental.setPayment(saved);
        rentalRepository.save(rental);
        makeCarAvailable(rental);

        return toResponse(saved);
    }

    @Transactional
    public PaymentResponse createPendingPayment(Long rentalId, String intentId, Long customerId) {
        Rental rental = getReturnedRental(rentalId, customerId);

        Payment payment = rental.getPayment() == null ? new Payment() : rental.getPayment();
        payment.setPaymentDate(LocalDate.now());
        payment.setAmount(rentalService.rentalCharge(rental));
        payment.setPaymentMethod("CARD");
        payment.setStripePaymentIntentId(intentId);
        payment.setStatus("PENDING");

        Payment saved = paymentRepo.save(payment);
        rental.setPayment(saved);
        rentalRepository.save(rental);

        return toResponse(saved);
    }

    public void validateCanCreateCardPayment(Long rentalId, Long customerId) {
        getReturnedRental(rentalId, customerId);
    }

    @Transactional
    public PaymentResponse markPaid(String intentId) {
        return markPaid(intentId, null);
    }

    @Transactional
    public PaymentResponse markPaid(String intentId, Long customerId) {
        Payment payment = paymentByIntentId(intentId);
        Rental rental = rentalForPayment(payment);
        checkOwner(rental, customerId);

        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDate.now());
        makeCarAvailable(rental);

        return toResponse(paymentRepo.save(payment));
    }

    @Transactional
    public PaymentResponse markFailed(String intentId) {
        Payment payment = paymentByIntentId(intentId);
        payment.setStatus("FAILED");
        return toResponse(paymentRepo.save(payment));
    }

    private Rental getReturnedRental(Long rentalId, Long customerId) {
        Rental rental = getRental(rentalId, customerId);
        if (!"RETURNED".equalsIgnoreCase(rental.getStatus())) {
            throw new RuntimeException("Payment allowed only for RETURNED rentals");
        }
        return rental;
    }

    private Rental getRental(Long rentalId, Long customerId) {
        Rental rental = rentalService.getRentalEntity(rentalId);
        checkOwner(rental, customerId);
        return rental;
    }

    private void checkOwner(Rental rental, Long customerId) {
        Customer customer = customerForRental(rental);
        if (customerId != null && (customer == null || !customerId.equals(customer.getCustomerId()))) {
            throw new RuntimeException("Customers can only access their own rentals");
        }
    }

    private void makeCarAvailable(Rental rental) {
        Car car = carForRental(rental);
        if (car != null) {
            car.setAvailability("AVAILABLE");
            carRepository.save(car);
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        Rental rental = rentalForPayment(payment);
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getStripePaymentIntentId(),
                rental == null ? null : rental.getRentalId()
        );
    }

    private Payment paymentByIntentId(String intentId) {
        return paymentRepo.findAll().stream()
                .filter(payment -> intentId != null && intentId.equals(payment.getStripePaymentIntentId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    private Rental rentalForPayment(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) return null;

        return rentalRepository.findAll().stream()
                .filter(rental -> rental.getPayment() != null)
                .filter(rental -> payment.getPaymentId().equals(rental.getPayment().getPaymentId()))
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
