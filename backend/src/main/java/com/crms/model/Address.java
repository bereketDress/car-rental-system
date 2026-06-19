package com.crms.model;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
