package com.crms.config;

import com.crms.model.Branch;
import com.crms.model.Car;
import com.crms.model.Customer;
import com.crms.model.Damage;
import com.crms.model.Manager;
import com.crms.model.Payment;
import com.crms.model.Rental;
import com.crms.model.Reservation;
import com.crms.model.Staff;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer entityFieldVisibility() {
        return builder -> builder
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .mixIn(Branch.class, BranchMixin.class)
                .mixIn(Car.class, CarMixin.class)
                .mixIn(Customer.class, CustomerMixin.class)
                .mixIn(Damage.class, DamageMixin.class)
                .mixIn(Manager.class, ManagerMixin.class)
                .mixIn(Payment.class, PaymentMixin.class)
                .mixIn(Rental.class, RentalMixin.class)
                .mixIn(Reservation.class, ReservationMixin.class)
                .mixIn(Staff.class, StaffMixin.class);
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "branchId")
    @JsonIgnoreProperties({"cars", "staff"})
    private static class BranchMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "carId")
    @JsonIgnoreProperties({"reservationCars", "rentals"})
    private static class CarMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "customerId")
    @JsonIgnoreProperties({"reservations", "rentals"})
    private static class CustomerMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "damageId")
    private static class DamageMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "managerId")
    @JsonIgnoreProperties({"staffList"})
    private static class ManagerMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "paymentId")
    private static class PaymentMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "rentalId")
    @JsonIgnoreProperties({"damages"})
    private static class RentalMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reservationId")
    @JsonIgnoreProperties({"reservationCars", "rentals"})
    private static class ReservationMixin {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "staffId")
    @JsonIgnoreProperties({"reservations", "payments"})
    private static class StaffMixin {}
}
