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
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    private String plateNumber;
    private String brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private String availability;
    private Double dailyRate;
    private String carType;

    @ManyToMany
    @JoinTable(
            name = "car_reservation",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id")
    )
    private List<Reservation> reservations;

    @OneToMany
    @JoinColumn(name="car_id")
    private List<Rental> rentals;
}
