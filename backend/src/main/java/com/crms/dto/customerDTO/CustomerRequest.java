package com.crms.dto.customerDTO;

import jakarta.validation.constraints.NotBlank;

public record
CustomerRequest(

        @NotBlank String name,
        @NotBlank String phone,
        @NotBlank String licenseNumber,
        @NotBlank String city,
        @NotBlank String street,
        @NotBlank String zipcode

) {}


