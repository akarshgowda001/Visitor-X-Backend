
package com.visitor_x.config;

import com.visitor_x.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow all preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Explicitly allow visitor registration POST from frontend
                        .requestMatchers(HttpMethod.POST, "/api/visitor/register").permitAll()
                        .requestMatchers("/api/visitor/register/test",
                                "/api/qr/generate-form",   // public: visitor scans QR → gets form URL
                                "/swagger-ui/**",
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/register",
                                "/api/photos/capture",
                                "/visitor-form.html",
                                "/swagger-ui.html",
                                "/error").permitAll()
                        .requestMatchers(
                                "/api/admin/**",
                                "/api/qr/generate",
                                "/api/photos/**",// admin-only: generate QR image
                                "/api/qr/save"             // admin-only: save QR to disk
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}