package com.crms.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    private String vinNumber;
    private String plateNumber;
    private String brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private String availability;
    private Double dailyRate;
    private String carType;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToMany(mappedBy = "reservationCars")
    @JsonIgnore
    private List<Reservation> reservationCars;

    @OneToMany(mappedBy = "car")
    @JsonIgnore
    private List<Rental> rentals;
}
