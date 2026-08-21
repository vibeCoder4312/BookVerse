package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.entity.Role;
import com.bookverse.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Every endpoint in this controller is admin-only - rather than repeat
// @PreAuthorize on each method, we put it once on the CLASS itself,
// which applies it to every method inside automatically.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // GET /api/admin/users?search=jane&page=0&size=20
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getUsers(search, pageable));
    }

    // PUT /api/admin/users/5/role?role=ADMIN
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> setUserRole(@PathVariable Long userId, @RequestParam Role role) {
        adminService.setUserRole(userId, role);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/admin/users/5/disable
    @PutMapping("/users/{userId}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long userId) {
        adminService.setUserEnabled(userId, false);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/admin/users/5/enable
    @PutMapping("/users/{userId}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable Long userId) {
        adminService.setUserEnabled(userId, true);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getAllReviews(pageable));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteAnyReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
