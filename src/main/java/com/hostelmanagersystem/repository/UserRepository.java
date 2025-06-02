package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.identity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



import java.util.Locale;

import java.util.List;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    Optional<List<User>> findAllByFirstNameContainingIgnoreCase(String firstName);
    Optional<User> findByPhone(String phone);


}
