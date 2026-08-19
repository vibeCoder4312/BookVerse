package com.bookverse.util;

import com.bookverse.entity.User;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// A small shared helper - any service that needs to know "who is making
// this request" calls currentUserService.getCurrentUser() instead of
// re-writing this SecurityContext lookup logic in Favorite/Library/History
// services separately.
@Component
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        // JwtAuthFilter (Phase 7) put the user's email here as the
        // "principal name" after verifying their token - this is how we
        // go from "a valid token was presented" to "here's the actual User row".
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
