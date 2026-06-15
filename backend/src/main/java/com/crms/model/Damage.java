package com.crms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Damage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long damageId;

    private LocalDate reportDate;
    private Double repairCost;
    private String status;
    private String description;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    @JsonIgnore
    private Rental rental;
}
