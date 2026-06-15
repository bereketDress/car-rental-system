package com.crms.security;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN))
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stripe/webhook").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stripe/config").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/confirm-card").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cars", "/api/cars/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cars").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("MANAGER")
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/branches/**").hasRole("MANAGER")
                        .requestMatchers("/api/staff/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/confirm").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/cancel").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/rentals/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/rentals/checkout").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/rentals/*/checkin").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers("/api/damages/**").hasAnyRole("STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/**").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/payments/create-intent").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/payments/record-card").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/payments/record").hasAnyRole("CUSTOMER", "STAFF", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/payments/process").hasAnyRole("STAFF", "MANAGER")
                        .anyRequest().authenticated()
                )

                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
