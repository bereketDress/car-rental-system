package com.crms.controller;
import lombok.*;
import com.crms.security.JwtUtil;
import com.crms.service.CustomerService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = normalize(body.get("email")).toLowerCase(Locale.ROOT);
        String password = normalize(body.get("password"));
        String role = normalize(body.get("role")).toUpperCase(Locale.ROOT); // CUSTOMER, STAFF, MANAGER

        if (!"CUSTOMER".equals(role) && !"MANAGER".equals(role) && !"STAFF".equals(role)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Role must be CUSTOMER, STAFF, or MANAGER"));
        }

        Optional<Map<String, Object>> login = switch (role) {
            case "CUSTOMER" -> loginCustomer(email, password);
            case "MANAGER" -> loginManager(email, password);
            case "STAFF" -> loginStaff(email, password);
            default -> Optional.empty();
        };

        return login
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Invalid email or password")));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(customerService.registerCustomerAccount(body));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Object details = authentication.getDetails();
        if (!(details instanceof Map<?, ?> detailMap)
                || !(detailMap.get("userId") instanceof Long userId)
                || !(detailMap.get("role") instanceof String role)) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        String email = authentication.getName();
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "email", email,
                "role", role,
                "userId", userId,
                "name", findDisplayName(role, userId).orElse(""),
                "capabilities", capabilitiesFor(role)
        ));
    }

    private Optional<Map<String, Object>> loginCustomer(String email, String password) {
        return loginSql(
                "SELECT customer_id AS id, name, password FROM customer WHERE email = ?",
                email,
                password,
                "CUSTOMER"
        );
    }

    private Optional<Map<String, Object>> loginManager(String email, String password) {
        return loginSql(
                "SELECT manager_id AS id, name, password FROM manager WHERE email = ?",
                email,
                password,
                "MANAGER"
        );
    }

    private Optional<Map<String, Object>> loginStaff(String email, String password) {
        return loginSql(
                "SELECT staff_id AS id, name, password FROM staff WHERE email = ? AND UPPER(role) = 'STAFF'",
                email,
                password,
                "STAFF"
        );
    }

    private Optional<Map<String, Object>> loginSql(String sql, String email, String password, String role) {
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next() || !passwordEncoder.matches(password, rs.getString("password"))) {
                return Optional.empty();
            }

            Long userId = rs.getLong("id");
            return Optional.of(Map.<String, Object>of(
                    "token", jwtUtil.generateToken(email, role, userId),
                    "role", role,
                    "userId", userId,
                    "name", rs.getString("name"),
                    "capabilities", capabilitiesFor(role)
            ));
        }, email);
    }

    private Optional<String> findDisplayName(String role, Long userId) {
        String sql = switch (role) {
            case "CUSTOMER" -> "SELECT name FROM customer WHERE customer_id = ?";
            case "MANAGER" -> "SELECT name FROM manager WHERE manager_id = ?";
            case "STAFF" -> "SELECT name FROM staff WHERE staff_id = ?";
            default -> null;
        };

        if (sql == null) {
            return Optional.empty();
        }

        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.ofNullable(rs.getString("name")) : Optional.empty(), userId);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> capabilitiesFor(String role) {
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
