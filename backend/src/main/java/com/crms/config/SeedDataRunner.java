package com.crms.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedDataRunner {

    private static final String INSERT_LOCAL_BRANCH =
            "INSERT INTO branch (name, phone, street, city, zipcode) " +
                    "SELECT ?, ?, ?, ?, ? " +
                    "WHERE NOT EXISTS (SELECT 1 FROM branch WHERE name = ?)";

    private static final String UPSERT_CUSTOMER =
            "INSERT INTO customer (name, email, phone, password, license_number, outstanding_balance) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (email) DO UPDATE SET " +
                    "name = EXCLUDED.name, " +
                    "phone = EXCLUDED.phone, " +
                    "password = EXCLUDED.password, " +
                    "license_number = EXCLUDED.license_number, " +
                    "outstanding_balance = EXCLUDED.outstanding_balance";

    private static final String UPSERT_STAFF =
            "INSERT INTO staff (name, role, email, phone, password, branch_id, manager_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (email) DO UPDATE SET " +
                    "name = EXCLUDED.name, " +
                    "role = EXCLUDED.role, " +
                    "phone = EXCLUDED.phone, " +
                    "password = EXCLUDED.password, " +
                    "branch_id = EXCLUDED.branch_id, " +
                    "manager_id = EXCLUDED.manager_id";

    private static final String UPSERT_MANAGER =
            "INSERT INTO manager (name, email, password, phone, branch_id) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (email) DO UPDATE SET " +
                    "name = EXCLUDED.name, " +
                    "password = EXCLUDED.password, " +
                    "phone = EXCLUDED.phone, " +
                    "branch_id = EXCLUDED.branch_id " +
                    "RETURNING manager_id";



    @Bean
    public CommandLineRunner seedCustomers(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            String encoded = passwordEncoder.encode("Test1234");

            jdbcTemplate.update(INSERT_LOCAL_BRANCH,
                    "Local Test Branch", "000-000-1000", "Local Test Street", "Local City", "0000", "Local Test Branch"
            );
            Long branchId = jdbcTemplate.queryForObject(
                    "SELECT branch_id FROM branch WHERE name = ? ORDER BY branch_id LIMIT 1",
                    Long.class,
                    "Local Test Branch"
            );

            jdbcTemplate.update(UPSERT_CUSTOMER, "Test User", "test@local", "000-000-0000", encoded, "TEST1234", 0.0
            );

            Long managerId = jdbcTemplate.queryForObject(
                    UPSERT_MANAGER,
                    Long.class,
                    "Test Manager", "manager@local", encoded, "000-000-0002", branchId
            );

            jdbcTemplate.update(UPSERT_STAFF, "Test Staff", "STAFF", "staff@local", "000-000-0001", encoded, branchId, managerId
            );
        };
    }
}
