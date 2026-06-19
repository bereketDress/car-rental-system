package com.crms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String name;
    private String email;
    private String password;
    private String phone;
    private String licenseNo;
    private Float outstandingBalance;

    @OneToMany
    @JoinColumn(name="customer_id")
    private List<Reservation> reservations;

    @OneToMany
    @JoinColumn(name="customer_id")
    private List<Rental> rentals;

    @Embedded
    private Address address;
}
