package com.crms.service;
import com.crms.model.Car;
import com.crms.model.Customer;
import lombok.RequiredArgsConstructor;
import com.crms.model.Payment;
import com.crms.model.Rental;
import com.crms.repository.CarRepository;
import com.crms.repository.CustomerRepository;
import com.crms.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalService rentalService;
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> listByCustomer(Long customerId) {
        return paymentRepository.findByRentalCustomerCustomerId(customerId);
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    @Transactional(readOnly = true)
    public Float computeCharges(Long rentalId) {
        return computeCharges(rentalId, null);
    }

    @Transactional(readOnly = true)
    public Float computeCharges(Long rentalId, Long customerId) {
        Rental rental = rentalService.getRental(rentalId);

        Customer customer = get(rental, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(customer, "customerId"))) {
            throw new RuntimeException("Customers can only view charges for their own rentals.");
        }

        return (float) rentalCharge(rental);
    }

    @Transactional
    public Payment createPendingPayment(Long rentalId, String stripePaymentIntentId) {
        return createPendingPayment(rentalId, stripePaymentIntentId, null);
    }

    @Transactional
    public Payment createPendingPayment(Long rentalId, String stripePaymentIntentId, Long customerId) {
        Rental rental = rentalService.getRental(rentalId);

        Customer customer = get(rental, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(customer, "customerId"))) {
            throw new RuntimeException("Customers can only pay their own rentals.");
        }

        if (!RENTAL_RETURNED.equalsIgnoreCase(string(rental, "status"))) {
            throw new RuntimeException("Payment can only be created after the rental is returned.");
        }

        Payment payment = paymentRepository.findByRentalRentalId(rentalId)
                .orElseGet(Payment::new);

        if (PAYMENT_COMPLETED.equalsIgnoreCase(paymentStatus(payment))) {
            throw new RuntimeException("Payment is already completed for this rental.");
        }

        set(payment, "rental", rental);
        set(payment, "amount", rentalCharge(rental));
        set(payment, "paymentMethod", "CARD");
        set(payment, "paymentDate", LocalDate.now());

        Payment saved = paymentRepository.save(payment);
        updatePaymentMetadata(saved, stripePaymentIntentId, PAYMENT_PENDING);
        return saved;
    }

    public Payment getByRental(Long rentalId) {
        return paymentRepository.findByRentalRentalId(rentalId)
                .orElseThrow(() -> new RuntimeException("Payment not found for rental: " + rentalId));
    }

    public boolean processPayment(Long rentalId,
                                  String paymentMethod,
                                  Float dailyRate,
                                  Long custId) {
        // Keeping existing logic for compatibility or internal use
        Rental rental = rentalService.getRental(rentalId);

        if (!RENTAL_RETURNED.equalsIgnoreCase(string(rental, "status"))) {
            throw new RuntimeException(
                    "Payment can only be processed for RETURNED rentals. Current status: "
                            + string(rental, "status")
            );
        }

        Payment payment = paymentRepository.findByRentalRentalId(rentalId)
                .orElseGet(Payment::new);
        set(payment, "rental", rental);
        set(payment, "amount", rentalCharge(rental));
        set(payment, "paymentMethod", paymentMethod);
        set(payment, "paymentDate", LocalDate.now());

        Payment saved = paymentRepository.save(payment);
        updatePaymentMetadata(saved, null, PAYMENT_COMPLETED);
        makeCarAvailable(rental);

        Customer customer = customerRepository.findById(custId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + custId));

        set(customer, "outstandingBalance", 0.0);
        customerRepository.save(customer);

        return true;
    }

    public void markPaid(String stripePaymentIntentId) {
        markPaid(stripePaymentIntentId, null);
    }

    public void markPaid(String stripePaymentIntentId, Long customerId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found for Intent ID: " + stripePaymentIntentId));

        Rental rental = get(payment, "rental", Rental.class);
        Customer customer = get(rental, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(customer, "customerId"))) {
            throw new RuntimeException("Customers can only confirm their own payments.");
        }

        set(payment, "paymentDate", LocalDate.now());
        Payment saved = paymentRepository.save(payment);
        updatePaymentMetadata(saved, stripePaymentIntentId, PAYMENT_COMPLETED);
        makeCarAvailable(rental);
    }

    public void markFailed(String stripePaymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found for Intent ID: " + stripePaymentIntentId));
        updatePaymentMetadata(payment, stripePaymentIntentId, PAYMENT_FAILED);
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment recordPayment(Long rentalId, String paymentMethod) {
        return recordPayment(rentalId, paymentMethod, null);
    }

    @Transactional
    public Payment recordPayment(Long rentalId, String paymentMethod, Long customerId) {
        if ("CARD".equalsIgnoreCase(paymentMethod) || "CREDIT_CARD".equalsIgnoreCase(paymentMethod)
                || "STRIPE".equalsIgnoreCase(paymentMethod)) {
            throw new RuntimeException("Card payments must be completed with Stripe.");
        }

        Rental rental = rentalService.getRental(rentalId);

        Customer customer = get(rental, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(customer, "customerId"))) {
            throw new RuntimeException("Customers can only pay their own rentals.");
        }

        if (!RENTAL_RETURNED.equalsIgnoreCase(string(rental, "status"))) {
            throw new RuntimeException("Payment can only be recorded after the rental is returned.");
        }

        Payment payment = paymentRepository.findByRentalRentalId(rentalId)
                .orElseGet(Payment::new);
        set(payment, "rental", rental);
        set(payment, "amount", rentalCharge(rental));
        set(payment, "paymentMethod", paymentMethod);
        set(payment, "paymentDate", LocalDate.now());
        Payment saved = paymentRepository.save(payment);
        updatePaymentMetadata(saved, null, PAYMENT_COMPLETED);
        makeCarAvailable(rental);
        return saved;
    }

    private void makeCarAvailable(Rental rental) {
        Car car = get(rental, "car", Car.class);
        if (car == null) {
            return;
        }

        setAvailable(car, true);
        carRepository.save(car);
    }

    private String paymentStatus(Payment payment) {
        return string(payment, "status");
    }

    private void updatePaymentMetadata(Payment payment, String stripePaymentIntentId, String status) {
        set(payment, "stripePaymentIntentId", stripePaymentIntentId);
        set(payment, "status", status);
        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public void validateCanCreateCardPayment(Long rentalId, Long customerId) {
        Rental rental = rentalService.getRental(rentalId);

        Customer customer = get(rental, "customer", Customer.class);
        if (customerId != null && !customerId.equals(longValue(customer, "customerId"))) {
            throw new RuntimeException("Customers can only pay their own rentals.");
        }

        if (!RENTAL_RETURNED.equalsIgnoreCase(string(rental, "status"))) {
            throw new RuntimeException("Payment can only be created after the rental is returned.");
        }

        paymentRepository.findByRentalRentalId(rentalId)
                .filter(payment -> PAYMENT_COMPLETED.equalsIgnoreCase(paymentStatus(payment)))
                .ifPresent(payment -> {
                    throw new RuntimeException("Payment is already completed for this rental.");
                });
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}
