package com.crms.service;

import com.crms.dto.auth.LoginRequest;
import com.crms.repository.CustomerRepository;
import com.crms.repository.ManagerRepository;
import com.crms.repository.StaffRepository;
import com.crms.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Optional<Map<String, Object>> login(LoginRequest request) {

        String email = request.email().trim().toLowerCase();
        String password = request.password();
        String role = request.role().trim().toUpperCase();

        if (role.equals("CUSTOMER")) {
            return customerRepository.findAll().stream()
                    .filter(customer -> email.equalsIgnoreCase(customer.getEmail()))
                    .findFirst()
                    .flatMap(customer -> checkPassword(
                            customer.getCustomerId(),
                            customer.getName(),
                            customer.getPassword(),
                            email,
                            password,
                            role
                    ));
        }

        if (role.equals("STAFF")) {
            return staffRepository.findAll().stream()
                    .filter(staff -> email.equalsIgnoreCase(staff.getEmail()))
                    .findFirst()
                    .flatMap(staff -> checkPassword(
                            staff.getStaffId(),
                            staff.getName(),
                            staff.getPassword(),
                            email,
                            password,
                            role
                    ));
        }

        if (role.equals("MANAGER")) {
            return managerRepository.findAll().stream()
                    .filter(manager -> email.equalsIgnoreCase(manager.getEmail()))
                    .findFirst()
                    .flatMap(manager -> checkPassword(
                            manager.getManagerId(),
                            manager.getName(),
                            manager.getPassword(),
                            email,
                            password,
                            role
                    ));
        }

        return Optional.empty();
    }

    private Optional<Map<String, Object>> checkPassword(
            Long userId,
            String name,
            String savedPassword,
            String email,
            String enteredPassword,
            String role
    ) {
        if (!passwordEncoder.matches(enteredPassword, savedPassword)) {
            return Optional.empty();
        }

        String token = jwtUtil.generateToken(email, role, userId);

        return Optional.of(Map.of(
                "token", token,
                "userId", userId,
                "name", name,
                "role", role,
                "capabilities", capabilitiesFor(role)
        ));
    }

    public List<String> capabilitiesFor(String role) {
        return switch (role) {
            case "CUSTOMER" -> List.of(
                    "REGISTER_ACCOUNT",
                    "SEARCH_VEHICLES",
                    "MAKE_RESERVATION",
                    "CANCEL_RESERVATION",
                    "VIEW_BOOKING_HISTORY"
            );
            case "STAFF" -> List.of(
                    "VIEW_RESERVATIONS",
                    "CONFIRM_RESERVATION",
                    "CANCEL_RESERVATION",
                    "CHECKOUT_RENTAL",
                    "CHECKOUT_VEHICLE",
                    "RECORD_START_MILEAGE",
                    "CHECKIN_RENTAL",
                    "CHECKIN_VEHICLE",
                    "RECORD_END_MILEAGE",
                    "RECORD_DAMAGE",
                    "PROCESS_PAYMENT",
                    "PROCESS_CUSTOMER_PAYMENT",
                    "VIEW_DAMAGES"
            );
            case "MANAGER" -> List.of(
                    "VIEW_REPORT",
                    "MANAGE_VEHICLE",
                    "MANAGE_VEHICLES",
                    "MANAGE_STAFF",
                    "VIEW_REPORTS",
                    "ADD_VEHICLE",
                    "UPDATE_VEHICLE",
                    "DELETE_VEHICLE",
                    "ADD_STAFF",
                    "UPDATE_STAFF",
                    "DELETE_STAFF"
            );
            default -> List.of();
        };
    }
}
