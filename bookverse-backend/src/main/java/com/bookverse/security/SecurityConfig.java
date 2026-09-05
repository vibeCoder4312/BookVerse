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

// @EnableMethodSecurity lets us use @PreAuthorize("hasRole('ADMIN')")
// on individual controller methods elsewhere in the app.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

    // CORS is configured HERE, inside Spring Security's own filter chain -
    // not via a separate WebMvcConfigurer. Security's filter chain runs
    // before Spring MVC ever sees a request, so it intercepts the browser's
    // CORS preflight OPTIONS requests before any MVC-level CORS config
    // would get a chance to apply. This was a real bug we hit earlier in
    // the project (authenticated POST/DELETE calls silently failing while
    // GET worked fine) - fixed by moving CORS handling to this layer.
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight requests carry no Authorization header by
                        // design - without this, Security would demand auth on a
                        // request that structurally can never have any.
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("GET", "/api/books/**").permitAll()
                        // NEW for Listen Stories: browsing stories is public,
                        // same as browsing books - only writing (admin) requires auth.
                        .requestMatchers("GET", "/api/stories/**").permitAll()
                        // Separate path prefix from /api/stories/** - needs its own
                        // rule, since Spring Security matches these independently.
                        .requestMatchers("GET", "/api/episodes/**").permitAll()
                        .requestMatchers("GET", "/api/story-categories/**").permitAll()
                        // BONUS FIX: these two were never actually added here despite
                        // CategoryController/AuthorController's GET endpoints having
                        // no @PreAuthorize - meaning "public reads" was never true,
                        // they silently required plain login this whole time. Fixed now.
                        .requestMatchers("GET", "/api/categories/**").permitAll()
                        .requestMatchers("GET", "/api/authors/**").permitAll()
                        .requestMatchers("GET", "/api/health").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
