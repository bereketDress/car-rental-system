package com.crms.repository;

import com.crms.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalRentalId(Long rentalId);
    @Query(value = "SELECT * FROM payment WHERE stripe_payment_intent_id = ?1", nativeQuery = true)
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    List<Payment> findByRentalCustomerCustomerId(Long customerId);
}
