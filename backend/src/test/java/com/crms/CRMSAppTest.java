package com.crms;


import com.crms.model.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CRMSAppTest {
    private static final String SEEDED_PASSWORD = "Test1234";
    private static final String SEEDED_PASSWORD_HASH =
            "$2a$10$r71PF/uSjFaXGgFN3WK67OGATjEFCWMH6vGpuzxfZfmVcJA5bdyfu";

    @Test
    void seededPasswordMatchesStoredBcryptHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(encoder.matches(SEEDED_PASSWORD, SEEDED_PASSWORD_HASH));
    }

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
        customer.setCustomerId(1L);
        customer.setName("Test User");
        customer.setOutstandingBalance(150.0F);

        assertTrue(customer.getOutstandingBalance() > 0);
    }

    @Test
    void carAvailabilityLogicWorks() {
        Car car = new Car();
        car.setCarId(1L);
        car.setAvailability("AVAILABLE");

        assertTrue(isAvailable(car));

        car.setAvailability("UNAVAILABLE");
        assertFalse(isAvailable(car));
    }

    @Test
    void reservationStatusTransitionsWork() {
        Reservation reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setStatus("PENDING");

        reservation.setStatus("CONFIRMED");
        assertEquals("CONFIRMED", reservation.getStatus());

        reservation.setStatus("CANCELLED");
        assertEquals("CANCELLED", reservation.getStatus());
    }

    @Test
    void reservationSerializationSkipsLazyCollections() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setStatus("PENDING");

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String json = objectMapper.writeValueAsString(reservation);

        assertFalse(json.contains("reservationCars"));
        assertFalse(json.contains("rentals"));
    }

    @Test
    void rentalSerializationSkipsBackReferenceCollections() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("Test User");

        Rental rental = new Rental();
        rental.setRentalId(1L);
        rental.setDamage(List.of(new Damage()));
        rental.setPayment(new Payment());

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String json = objectMapper.writeValueAsString(rental);

        assertFalse(json.contains("reservationCars"));
        assertFalse(json.contains("\"rentals\""));
        assertTrue(json.contains("\"payment\""));
        assertTrue(json.contains("\"damage\""));
    }

    @Test
    void carSerializationSkipsBranchLazyCollections() throws Exception {
        Branch branch = new Branch();
        branch.setBranchId(1L);
        branch.setName("Main Branch");

        Car car = new Car();
        car.setCarId(1L);
        branch.setCars(List.of(car));

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String json = objectMapper.writeValueAsString(car);

        assertFalse(json.contains("\"branch\""));
    }

    @Test
    void damageUpdateStatusWorks() {
        Damage damage = new Damage();
        damage.setDamageId(1L);
        damage.setStatus("REPORTED");

        damage.setStatus("REPAIRED");
        assertEquals("REPAIRED", damage.getStatus());
    }

    private boolean isAvailable(Car car) {
        return "AVAILABLE".equalsIgnoreCase(car.getAvailability())
                || "TRUE".equalsIgnoreCase(car.getAvailability());
    }
}
