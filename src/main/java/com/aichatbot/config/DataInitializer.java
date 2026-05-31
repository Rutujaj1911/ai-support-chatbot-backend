package com.aichatbot.config;

import com.aichatbot.entity.Role;
import com.aichatbot.entity.Role.ERole;
import com.aichatbot.entity.User;
import com.aichatbot.repository.RoleRepository;
import com.aichatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create roles if not exist
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, ERole.ROLE_USER));
            log.info("Created ROLE_USER");
        }
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, ERole.ROLE_ADMIN));
            log.info("Created ROLE_ADMIN");
        }

        // Create default admin
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();
            User admin = User.builder()
                .username("admin")
                .email("admin@aichatbot.com")
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("System Administrator")
                .roles(Set.of(adminRole))
                .build();
            userRepository.save(admin);
            log.info("Created default admin user: admin / Admin@123");
        }
    }
}
