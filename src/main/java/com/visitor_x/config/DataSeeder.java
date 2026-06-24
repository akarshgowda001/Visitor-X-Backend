package com.visitor_x.config;

import com.visitor_x.entity.Admin;
import com.visitor_x.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAdmin(AdminRepository adminRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.findByUsername("admin").isEmpty()) {
                Admin admin = Admin.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role("ADMIN")
                        .build();
                adminRepository.save(admin);
                System.out.println("Default admin seeded.");
            }
        };
    }
}
