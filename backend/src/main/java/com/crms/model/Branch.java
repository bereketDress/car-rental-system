package com.crms.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "branch")
@JsonIgnoreProperties({"cars", "staff"})
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;

    private String name;
    private String phone;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Car> cars;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Staff> staff;
}
