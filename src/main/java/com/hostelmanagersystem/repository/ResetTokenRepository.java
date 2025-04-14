package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.identity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
}
