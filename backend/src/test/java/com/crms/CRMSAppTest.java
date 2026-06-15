package com.crms;


import com.crms.model.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.crms.util.EntityFields.*;


class CRMSAppTest {

    @Test
    void branchAddressIsHandled() {
        Branch branch = new Branch();
        branch.setName("Main Branch");
        Address address = Address.builder()
                .city("New York")
                .street("5th Ave")
                .zipcode("10001")
                .build();
        branch.setAddress(address);

        assertNotNull(branch.getAddress());
        assertEquals("New York", branch.getAddress().getCity());
        assertEquals("5th Ave", branch.getAddress().getStreet());
        assertEquals("10001", branch.getAddress().getZipcode());
    }

    @Test
    void customerOutstandingBalanceIsDetected() {
        Customer customer = new Customer();
        set(customer, "customerId", 1L);
        set(customer, "name", "Test User");
        set(customer, "outstandingBalance", 150.0);

        assertTrue(doubleValue(customer, "outstandingBalance") > 0);
    }

    @Test
    void carAvailabilityLogicWorks() {
        Car car = new Car();
        set(car, "vinNumber", "VIN123");
        setAvailable(car, true);

        assertTrue(available(car));

        setAvailable(car, false);
        assertFalse(available(car));
    }

    @Test
    void reservationStatusTransitionsWork() {
        Reservation reservation = new Reservation();
        set(reservation, "reservationId", 1L);
        set(reservation, "status", "PENDING");

        set(reservation, "status", "CONFIRMED");
        assertEquals("CONFIRMED", string(reservation, "status"));

        set(reservation, "status", "CANCELLED");
        assertEquals("CANCELLED", string(reservation, "status"));
    }

    @Test
    void reservationSerializationSkipsLazyCollections() throws Exception {
        Reservation reservation = new Reservation();
        set(reservation, "reservationId", 1L);
        set(reservation, "status", "PENDING");

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String json = objectMapper.writeValueAsString(reservation);

        assertFalse(json.contains("reservationCars"));
        assertFalse(json.contains("rentals"));
    }

    @Test
    void rentalSerializationSkipsBackReferenceCollections() throws Exception {
        Customer customer = new Customer();
        set(customer, "customerId", 1L);
        set(customer, "name", "Test User");

        Car car = new Car();
        set(car, "carId", 1L);
        set(car, "vinNumber", "VIN123");

        Reservation reservation = new Reservation();
        set(reservation, "reservationId", 1L);
        set(reservation, "status", "CONVERTED");

        Rental rental = new Rental();
        set(rental, "rentalId", 1L);
        set(rental, "customer", customer);
        set(rental, "car", car);
        set(rental, "reservation", reservation);

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String json = objectMapper.writeValueAsString(rental);

        assertFalse(json.contains("reservationCars"));
        assertFalse(json.contains("\"rentals\""));
        assertFalse(json.contains("\"payment\""));
        assertFalse(json.contains("\"damages\""));
    }

    @Test
    void carSerializationSkipsBranchLazyCollections() throws Exception {
        Branch branch = new Branch();
        branch.setBranchId(1L);
        branch.setName("Main Branch");

        Car car = new Car();
        set(car, "carId", 1L);
        set(car, "vinNumber", "VIN123");
        set(car, "branch", branch);

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String json = objectMapper.writeValueAsString(car);

        assertFalse(json.contains("\"cars\""));
        assertFalse(json.contains("\"staff\""));
    }

    @Test
    void damageUpdateStatusWorks() {
        Damage damage = new Damage();
        set(damage, "damageId", 1L);
        set(damage, "status", "REPORTED");

        set(damage, "status", "REPAIRED");
        assertEquals("REPAIRED", string(damage, "status"));
    }
}
