package com.crms.model;

import com.crms.model.Reservation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    private LocalDate checkoutDate;
    private LocalDate returnDate;
    private Integer startMileage;
    private Integer endMileage;
    private String status;

    @Transient
    private Double baseCharge;

    @Transient
    private Double damageRepairCost;

    @Transient
    private Double totalCharge;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToOne(mappedBy = "rental")
    @JsonIgnore
    private Payment payment;

    @OneToMany(mappedBy = "rental")
    @JsonIgnore
    private List<Damage> damages;
}
