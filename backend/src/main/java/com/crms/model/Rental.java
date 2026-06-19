package com.crms.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    private LocalDate checkoutDate;
    private LocalDate returnDate;
    private Integer startMileage;
    private Integer endMileage;
    private String status;
    private Float baseCharge;

    @OneToMany
    @JoinColumn(name="rental_id")
    private List<Damage>damage;

    @OneToOne
    @JoinColumn(name = "rental_id")
    private Payment payment;
}
