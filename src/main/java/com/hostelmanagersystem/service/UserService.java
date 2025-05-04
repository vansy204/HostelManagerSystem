package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.CreateUserRequest;
import com.hostelmanagersystem.dto.request.UpdateUserRequest;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.identity.User;

import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;

import com.hostelmanagersystem.mapper.UserMapper;
import com.hostelmanagersystem.repository.RoleRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.toUser(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        var role = roleRepository.findById(createUserRequest.getRoleName())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.setRole(role);
        user.setCreateAt(Instant.now());
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        var result = userMapper.toUserResponse(user);
        result.setRoleName(role.getName());
        return result;
    }



    public UserResponse getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName(); // hoặc lấy từ token
        log.info("Get current user by id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }


    public UserResponse updateUser(UpdateUserRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getUserName() != null) user.setUserName(request.getUserName());

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

}
