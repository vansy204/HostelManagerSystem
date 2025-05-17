package com.hostelmanagersystem.entity.identity;

import com.hostelmanagersystem.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(
            name = "username",
            unique = true,
            columnDefinition =
                    "VARCHAR(255) COLLATE utf8mb4_unicode_ci")

    String userName;
    String password;
    @Column(name = "email",unique = true)
    String email;
    @Column(
            name = "firstName",
            columnDefinition =
                    "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String firstName;
    @Column(
            name = "lastName",
            columnDefinition =
                    "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String lastName;
    @Column(
            name = "phone",
            unique = true,
            columnDefinition =
                    "VARCHAR(11)")
    String phone;

    @Column(name = "create_at")
    Instant createAt;


    Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;
}
