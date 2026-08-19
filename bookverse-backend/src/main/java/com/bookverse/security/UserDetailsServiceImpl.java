package com.bookverse.security;

import com.bookverse.entity.User;
import com.bookverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// Spring Security doesn't know anything about OUR "User" entity - it
// works with its own UserDetails interface. This class is the bridge:
// given an email, look up our User in the database, and hand back an
// object shaped the way Spring Security understands (username, password,
// and a list of "authorities" i.e. roles/permissions).
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        // Spring Security expects roles prefixed with "ROLE_" by convention
        // (e.g. "ROLE_ADMIN") - this is what @PreAuthorize("hasRole('ADMIN')")
        // checks against later in Phase 11.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword()) // already BCrypt-hashed
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
