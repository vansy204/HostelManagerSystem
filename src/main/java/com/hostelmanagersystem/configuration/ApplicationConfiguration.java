package com.hostelmanagersystem.configuration;

import com.hostelmanagersystem.entity.identity.Role;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.repository.RoleRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Bean
    ApplicationRunner runner(UserRepository userRepository) {
        return args -> {
           Role userRole = roleRepository.save(
                    Role.builder()
                            .name("USER")
                            .description("User Role")
                            .build());
            Role adminRole = roleRepository.save(
                    Role.builder()
                            .name("ADMIN")
                            .description("Admin Role")
                            .build());
            if(userRepository.findByUserName("admin").isEmpty()){
                User admin = User.builder()
                        .userName("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("phamvansy204@gmail.com")
                        .role(adminRole)
                        .build();
                userRepository.save(admin);
                log.info("admin user created with default password admin");
            }
        };
    }

}
