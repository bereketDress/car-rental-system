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

public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    private String name;
    private String phone;

    @Embedded
    private Address address;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_id")
    private List<Car> cars;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_id")
    private List<Staff> staffs;

    @OneToOne
    @JoinColumn(name= "branch_id")
    private Manager manager;

}