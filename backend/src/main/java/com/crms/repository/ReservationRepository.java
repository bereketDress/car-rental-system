package com.crms.repository;
import com.crms.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerCustomerId(Long custId);
    List<Reservation> findByStatus(String status);

    @Query("""
            select distinct r
            from Reservation r
            left join fetch r.customer
            left join fetch r.staff
            left join fetch r.reservationCars c
            left join fetch c.branch
            """)
    List<Reservation> findAllWithDetails();

    @Query("""
            select distinct r
            from Reservation r
            left join fetch r.customer
            left join fetch r.staff
            left join fetch r.reservationCars c
            left join fetch c.branch
            where r.customer.customerId = :customerId
            """)
    List<Reservation> findByCustomerCustomerIdWithDetails(@Param("customerId") Long customerId);

    @Query("""
            select distinct r
            from Reservation r
            left join fetch r.customer
            left join fetch r.staff
            left join fetch r.reservationCars c
            left join fetch c.branch
            where r.reservationId = :reservationId
            """)
    Optional<Reservation> findByIdWithDetails(@Param("reservationId") Long reservationId);
}
