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
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private LocalDate reservationDate;
    private LocalDate pickupDate;
    private String status;
    @OneToOne
    @JoinColumn(name="reservation_id")
    private Rental rental;

    @ManyToMany(mappedBy="reservations")
    private List<Car> cars;

}
