package com.crms.service;

import com.crms.dto.customer.CustomerRequest;
import com.crms.dto.customer.CustomerResponse;
import com.crms.model.Address;
import com.crms.model.Customer;
import com.crms.repository.CustomerRepository;
import com.crms.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<CustomerResponse> listAll() {
        return customerRepository.findAll().stream().map(this::response).toList();
    }

    public CustomerResponse getCustomer(Long id) {
        return response(getCustomerEntity(id));
    }

    public CustomerResponse registerCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        fill(customer, request);
        customer.setOutstandingBalance(0.0F);
        return response(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getCustomerEntity(id);
        fill(customer, request);
        return response(customerRepository.save(customer));
    }

    public boolean deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        return true;
    }

    public boolean hasOutstandingBalance(Long customerId) {
        Float balance = getCustomerEntity(customerId).getOutstandingBalance();
        return balance != null && balance > 0;
    }

    public boolean eligibleForReservation(Long customerId) {
        return !hasOutstandingBalance(customerId);
    }

    public Map<String, Object> registerCustomerAccount(Map<String, String> body) {
        String name = text(body.get("name"));
        String email = text(body.get("email")).toLowerCase();
        String password = text(body.get("password"));

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, email, and password are required");
        }

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM customer WHERE email = ?",
                Integer.class,
                email
        );

        if (count != null && count > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setPhone(text(body.get("phone")));
        customer.setLicenseNo(text(body.get("licenseNumber")));
        customer.setOutstandingBalance(0.0F);
        customer.setAddress(address(text(body.get("city")), text(body.get("street")), text(body.get("zipcode"))));

        Customer saved = customerRepository.save(customer);
        return Map.of(
                "token", jwtUtil.generateToken(email, "CUSTOMER", saved.getCustomerId()),
                "role", "CUSTOMER",
                "userId", saved.getCustomerId(),
                "name", saved.getName(),
                "capabilities", List.of("REGISTER_ACCOUNT", "SEARCH_VEHICLES", "MAKE_RESERVATION",
                        "CANCEL_RESERVATION", "VIEW_BOOKING_HISTORY")
        );
    }

    Customer getCustomerEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    private void fill(Customer customer, CustomerRequest request) {
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setLicenseNo(request.licenseNumber());
        customer.setAddress(address(request.city(), request.street(), request.zipcode()));
    }

    private Address address(String city, String street, String zipcode) {
        return Address.builder().city(city).street(street).zipcode(zipcode).build();
    }

    private CustomerResponse response(Customer customer) {
        return new CustomerResponse(customer.getCustomerId(), customer.getName(), customer.getPhone(),
                customer.getLicenseNo(), customer.getOutstandingBalance(), List.of(), List.of());
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
