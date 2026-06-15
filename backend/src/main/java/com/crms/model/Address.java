package com.crms.model;

import jakarta.persistence.*;

import lombok.*;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private Long addressId;

    private String city;
    private String street;
    private String zipcode;
}
