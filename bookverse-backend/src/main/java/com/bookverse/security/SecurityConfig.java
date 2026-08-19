package com.bookverse.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @EnableWebSecurity turns on Spring Security's web protection.
// @EnableMethodSecurity lets us use @PreAuthorize("hasRole('ADMIN')")
// directly on controller methods later (Phase 11).
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // BCrypt is a one-way hashing algorithm designed specifically for
    // passwords - it's slow ON PURPOSE (makes brute-forcing expensive)
    // and automatically handles "salting" so two users with the same
    // password get completely different stored hashes.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // The AuthenticationManager is what actually checks "does this email
    // + password combination match a real user?" during login - it uses
    // our UserDetailsServiceImpl + passwordEncoder together to do that.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // IMPORTANT: this is DIFFERENT from the CorsConfig.java we wrote in
    // Phase 6. That one only configures CORS for Spring MVC's own request
    // handling - but Spring Security's filter chain runs BEFORE a request
    // ever reaches MVC, so Security needs to know about CORS separately,
    // or it blocks the browser's preflight OPTIONS request itself before
    // our MVC-level config ever gets a chance to run.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tell Spring Security to actually USE the CORS bean above,
                // instead of ignoring CORS entirely and blocking preflight
                // requests under its default "everything needs auth" rule.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF protection defends against browser-based form attacks that
                // rely on cookies/sessions. We use stateless JWTs instead, sent
                // manually in headers, so CSRF doesn't apply here - safe to disable.
                .csrf(csrf -> csrf.disable())

                // Tells Spring Security: "never create or use an HTTP session."
                // Every single request must prove who it is via its own JWT -
                // that's what makes this a truly stateless API.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Preflight requests carry NO Authorization header by design -
                        // browsers send them before the real request to ask permission.
                        // Without this line, Security would demand auth on a request
                        // that structurally can never have any, blocking every single
                        // authenticated POST/PUT/DELETE call from the browser.
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Auth endpoints must be reachable BEFORE you have a token
                        .requestMatchers("/api/auth/**").permitAll()
                        // Public browsing - anyone can view books without logging in
                        .requestMatchers("GET", "/api/books/**").permitAll()
                        .requestMatchers("GET", "/api/health").permitAll()
                        // Everything else requires a valid JWT
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())
                // Insert our custom JWT check BEFORE Spring's default username/password
                // filter - so by the time Spring tries its normal login flow, we've
                // already authenticated the request via the token if one was present.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
