package com.crms.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private static final String[] PUBLIC = {
            "/api/auth/**",
            "/api/stripe/config",
            "/api/customers/register"
    };

    private static final String[] MANAGER = {
            "/api/manager/**",
            "/api/branches/**",
            "/api/staff/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(PUBLIC).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stripe/webhook").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/confirm-card").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cars", "/api/cars/search").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/cars").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("MANAGER")
                        .requestMatchers(MANAGER).hasRole("MANAGER")

                        .requestMatchers(HttpMethod.POST, "/api/rentals/checkout").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/rentals/*/checkin").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("CUSTOMER")
                        .requestMatchers("/api/reservations/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers("/api/rentals/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers("/api/payments/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")

                        .requestMatchers("/api/damages/**").hasAnyRole("STAFF", "MANAGER")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        c.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
