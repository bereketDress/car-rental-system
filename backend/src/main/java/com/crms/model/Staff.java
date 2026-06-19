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
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    private String name;
    private String role;
    private String email;
    private String phone;
    private String password;

    @OneToMany
    @JoinColumn(name="staff_id")
    private List<Payment> payments;

    @OneToMany
    @JoinColumn(name="staff_id")
   private  List<Reservation> reservations;


}
