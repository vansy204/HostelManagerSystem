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
            Role tenantRole = roleRepository.save(
                    Role.builder()
                            .name(String.valueOf(RoleEnum.TENANT))
                            .description("tenant Role")
                            .build());
            if(userRepository.findByUserName("admin").isEmpty()){
                User admin = User.builder()
                        .userName("admin")
                        .password(passwordEncoder.encode("admin"))
                        .firstName("admin")
                        .lastName("")

                        .role(adminRole)
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
                        .firstName("owner")
                        .lastName("1")
                        .isActive(true)
                        .build();
                userRepository.save(owner);
                log.info("tenant user created with default username password tenant");
            }
            if(userRepository.findByUserName("tenant1").isEmpty()){
                User tenant = User.builder()
                        .userName("tenant1")
                        .password(passwordEncoder.encode("tenant1"))
                        .role(tenantRole)
                        .firstName("tenant1")
                        .lastName("1")
                        .isActive(true)
                        .build();
                userRepository.save(tenant);
                log.info("tenant user created with default username password tenant");

            }
        };
    }

}
