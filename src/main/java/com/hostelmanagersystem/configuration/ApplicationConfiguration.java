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

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Bean
    ApplicationRunner runner(UserRepository userRepository) {
        return args -> {
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
            if(userRepository.findByUserName("owner1").isEmpty()){
                User owner = User.builder()
                        .userName("owner1")
                        .password(passwordEncoder.encode("owner1"))
                        .role(ownerRole)
                        .build();
                userRepository.save(owner);
                log.info("renter user created with default username password renter1");
            }
            if(userRepository.findByUserName("renter1").isEmpty()){
                User renter = User.builder()
                        .userName("renter1")
                        .password(passwordEncoder.encode("renter1"))
                        .role(renterRole)
                        .build();
                userRepository.save(renter);
                log.info("tenant user created with default username password tenant1");

            }
        };
    }

}
