package com.crms.repository;

import com.crms.model.Damage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DamageRepository extends JpaRepository<Damage, Long> {
    List<Damage> findByRentalRentalId(Long rentalId);
}
