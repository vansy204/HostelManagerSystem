package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.identity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
