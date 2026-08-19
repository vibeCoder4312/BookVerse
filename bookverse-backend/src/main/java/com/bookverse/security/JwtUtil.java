package com.bookverse.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// A small toolbox class for everything JWT-related: creating a token,
// and later reading/validating one. Nothing in here is Spring-Security
// specific - it's pure "how do JWTs work" logic.
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        // Converts our plain-text secret string into a proper cryptographic
        // key object that the signing algorithm (HMAC-SHA) can use.
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String email) {
        return buildToken(email, accessExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, refreshExpirationMs);
    }

    private String buildToken(String email, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)        // "who" this token belongs to
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey()) // the signature - proves we made this token
                .compact();             // turns it into the final string format
    }

    // Reads the email back out of a token - but ONLY succeeds if the
    // token's signature is valid, meaning it wasn't tampered with.
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Any parsing failure (bad signature, malformed token, expired) = invalid
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
