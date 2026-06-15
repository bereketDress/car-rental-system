package com.crms.controller;
import com.crms.dto.reservationDto.ReservationRequest;
import com.crms.dto.reservationDto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import com.crms.model.Reservation;
import com.crms.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

import static com.crms.util.EntityFields.get;
import static com.crms.util.EntityFields.longValue;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> list(@RequestParam(required = false) Long customerId,
                                                          Authentication authentication) {
        Long authenticatedCustomerId = authenticatedCustomerId(authentication);
        if (authenticatedCustomerId != null) {
            return ResponseEntity.ok(reservationService.listResponsesByCustomer(authenticatedCustomerId));
        }
        if (customerId != null && hasStaffAccess(authentication)) {
            return ResponseEntity.ok(reservationService.listResponsesByCustomer(customerId));
        }
        return ResponseEntity.ok(reservationService.getAllResponses());
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest request, Authentication authentication) {
        Long customerId = authenticatedCustomerId(authentication);
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can create reservations");
        }

        return ResponseEntity.ok(reservationService.createReservationResponse(
                customerId,
                request.vinNumber(),
                request.pickupDate()
        ));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservationResponse(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable Long id, Authentication authentication) {
        return cancelReservation(id, authentication);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelByPost(@PathVariable Long id, Authentication authentication) {
        return cancelReservation(id, authentication);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelByPut(@PathVariable Long id, Authentication authentication) {
        return cancelReservation(id, authentication);
    }

    private ResponseEntity<ReservationResponse> cancelReservation(Long id, Authentication authentication) {
        Reservation reservation = reservationService.getById(id);
        Long customerId = authenticatedCustomerId(authentication);
        Object reservationCustomer = get(reservation, "customer");

        if (customerId != null && (reservationCustomer == null
                || !customerId.equals(longValue(reservationCustomer, "customerId")))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customers can only cancel their own reservations");
        }

        return ResponseEntity.ok(reservationService.cancelReservationResponse(id));
    }

    private Long authenticatedCustomerId(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_CUSTOMER".equals(authority.getAuthority()))) {
            return null;
        }

        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> map && map.get("userId") instanceof Long userId) {
            return userId;
        }

        return null;
    }

    private boolean hasStaffAccess(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STAFF".equals(authority.getAuthority())
                        || "ROLE_MANAGER".equals(authority.getAuthority()));
    }
}
