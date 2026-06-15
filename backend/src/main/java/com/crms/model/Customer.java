package com.crms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String name;
    private String phone;
    private String licenseNumber;
    private Double outstandingBalance;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Rental> rentals;
}
