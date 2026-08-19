package com.bookverse.service.impl;

import com.bookverse.dto.*;
import com.bookverse.entity.RefreshToken;
import com.bookverse.entity.Role;
import com.bookverse.entity.User;
import com.bookverse.exception.BadRequestException;
import com.bookverse.exception.DuplicateResourceException;
import com.bookverse.exception.InvalidCredentialsException;
import com.bookverse.repository.RefreshTokenRepository;
import com.bookverse.repository.UserRepository;
import com.bookverse.security.JwtUtil;
import com.bookverse.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                // NEVER store the raw password - passwordEncoder.encode() runs
                // it through BCrypt, turning it into a one-way hash.
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER) // registration always creates a normal user, never an admin
                .build();

        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        try {
            // This line does the actual work: it runs our UserDetailsServiceImpl
            // to look the user up, then checks the given password against the
            // stored BCrypt hash. Throws BadCredentialsException if either is wrong -
            // note we deliberately give the SAME error either way (see catch below),
            // so we never reveal whether it was the email or password that was wrong.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        return issueTokens(user);
    }

    @Override
    public AuthResponseDTO refresh(RefreshRequestDTO dto) {
        RefreshToken stored = refreshTokenRepository.findByToken(dto.getRefreshToken())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByToken(stored.getToken());
            throw new InvalidCredentialsException("Refresh token has expired, please log in again");
        }

        User user = stored.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail());

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(stored.getToken()) // we reuse the same refresh token here
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public void logout(RefreshRequestDTO dto) {
        refreshTokenRepository.deleteByToken(dto.getRefreshToken());
    }

    // Shared by register() and login() - both end with "give this user
    // a fresh pair of tokens", so we avoid repeating the logic twice.
    private AuthResponseDTO issueTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
