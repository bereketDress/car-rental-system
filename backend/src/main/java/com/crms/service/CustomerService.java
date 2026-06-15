package com.crms.service;
import lombok.RequiredArgsConstructor;
import com.crms.model.Address;
import com.crms.model.Car;
import com.crms.model.Customer;
import com.crms.repository.CarRepository;
import com.crms.repository.CustomerRepository;
import com.crms.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<Customer> listAll() { return customerRepository.findAll(); }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    public Customer registerCustomer(Customer customer) {
        if (isBlank(string(customer, "name"))) {
            throw new IllegalArgumentException("Name is required");
        }
        set(customer, "name", string(customer, "name").trim());
        set(customer, "outstandingBalance", 0.0);
        return customerRepository.save(customer);
    }

    public Map<String, Object> registerCustomerAccount(Map<String, String> body) {
        String role = normalize(body.getOrDefault("role", "CUSTOMER")).toUpperCase(Locale.ROOT);
        if (!"CUSTOMER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only customer self-registration is allowed");
        }

        String name = normalize(body.get("name"));
        String email = normalize(body.get("email")).toLowerCase(Locale.ROOT);
        String password = normalize(body.get("password"));

        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, email, and password are required");
        }

        Integer existingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM customer WHERE email = ?",
                Integer.class,
                email
        );
        if (existingCount != null && existingCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        Customer customer = new Customer();
        set(customer, "name", name);
        set(customer, "phone", normalize(body.get("phone")));
        set(customer, "licenseNumber", normalize(body.get("licenseNumber")));
        set(customer, "outstandingBalance", 0.0);
        set(customer, "address", Address.builder()
                .city(normalize(body.get("city")))
                .street(normalize(body.get("street")))
                .zipcode(normalize(body.get("zipcode")))
                .build());

        Customer saved = customerRepository.save(customer);
        Long customerId = longValue(saved, "customerId");

        jdbcTemplate.update(
                "UPDATE customer SET email = ?, password = ? WHERE customer_id = ?",
                email,
                passwordEncoder.encode(password),
                customerId
        );

        return Map.of(
                "token", jwtUtil.generateToken(email, "CUSTOMER", customerId),
                "role", "CUSTOMER",
                "userId", customerId,
                "name", name,
                "capabilities", customerCapabilities()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> customerCapabilities() {
        return List.of(
                "REGISTER_ACCOUNT",
                "SEARCH_VEHICLES",
                "MAKE_RESERVATION",
                "CANCEL_RESERVATION",
                "VIEW_BOOKING_HISTORY"
        );
    }

    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = getCustomer(id);
        set(existing, "name", string(updated, "name"));
        set(existing, "phone", string(updated, "phone"));
        set(existing, "licenseNumber", string(updated, "licenseNumber"));
        set(existing, "outstandingBalance", doubleValue(updated, "outstandingBalance"));
        set(existing, "address", get(updated, "address"));
        return customerRepository.save(existing);
    }

    public boolean deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        return true;
    }


    public boolean hasOutstandingBalance(Long custId) {
        Customer customer = getCustomer(custId);
        Double outstandingBalance = doubleValue(customer, "outstandingBalance");
        return outstandingBalance != null && outstandingBalance > 0;
    }
    public boolean eligibleForReservation(Long custId) {
        return !hasOutstandingBalance(custId);
    }

    public List<Car> searchAvailableCars(String carType) {
        List<Car> allCars = carRepository.findAll();
        return allCars.stream()
                .filter(car -> available(car))
                .filter(car -> carType == null || carType.isBlank()
                        || carType.equalsIgnoreCase(string(car, "carType")))
                .toList();
    }
}
