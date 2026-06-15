package com.crms.repository;
import com.crms.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStatus(String status);
    List<Rental> findByCustomerCustomerId(Long custId);
    Optional<Rental> findByReservationReservationId(Long reservationId);
}
