package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.identity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

}
