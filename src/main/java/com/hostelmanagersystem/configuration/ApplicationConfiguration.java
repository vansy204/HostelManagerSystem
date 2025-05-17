package com.hostelmanagersystem.configuration;

import com.hostelmanagersystem.entity.identity.Role;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.enums.RoleEnum;
import com.hostelmanagersystem.repository.RoleRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Bean
    ApplicationRunner runner(UserRepository userRepository) {
        return args -> {
            Role adminRole = roleRepository.save(
                    Role.builder()
                            .name(String.valueOf(RoleEnum.ADMIN))
                            .description("admin Role")
                            .build()
            );
           Role ownerRole = roleRepository.save(
                    Role.builder()
                            .name(String.valueOf(RoleEnum.OWNER))
                            .description("owner Role")
                            .build());
            Role renterRole = roleRepository.save(
                    Role.builder()
                            .name(String.valueOf(RoleEnum.RENTER))
                            .description("Renter Role")
                            .build());
            if(userRepository.findByUserName("admin").isEmpty()){
                User admin = User.builder()
                        .userName("admin")
                        .firstName("admin")
                        .lastName("")
                        .email("phamvansy204@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .role(adminRole)
                        .createAt(Instant.now())
                        .isActive(true)
                        .build();
                userRepository.save(admin);
                log.info("admin user created with default username password admin");
            }
            if(userRepository.findByUserName("owner1").isEmpty()){
                User owner = User.builder()
                        .userName("owner1")
                        .password(passwordEncoder.encode("owner1"))
                        .role(ownerRole)
                        .firstName("owner1")
                        .lastName("")
                        .email("owner1@gmail.com")
                        .createAt(Instant.now())
                        .isActive(true)
                        .build();
                userRepository.save(owner);
                log.info("renter user created with default username password renter1");
            }
            if(userRepository.findByUserName("renter1").isEmpty()){
                User renter = User.builder()
                        .userName("renter1")
                        .password(passwordEncoder.encode("renter1"))
                        .role(renterRole)
                        .firstName("renter1")
                        .lastName("")
                        .email("renter1@gmail.com")
                        .createAt(Instant.now())
                        .isActive(true)
                        .build();
                userRepository.save(renter);
                log.info("tenant user created with default username password tenant1");

            }
        };
    }

}
