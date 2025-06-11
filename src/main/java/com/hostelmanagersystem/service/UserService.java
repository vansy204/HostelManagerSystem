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
import java.time.LocalDateTime;
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
        user.setCreateAt(LocalDateTime.now());
        user.setIsActive(true);
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
       var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));


        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(String userId){
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }


    public UserResponse updateUser(UpdateUserRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(existing -> {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                    });
            user.setEmail(request.getEmail());
        }


        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            userRepository.findByPhone(request.getPhone())
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(existing -> {
                        throw new AppException(ErrorCode.PHONE_EXISTED);
                    });
            user.setPhone(request.getPhone());
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getUserName() != null && !request.getUserName().equals(user.getUserName()))  {
            userRepository.findByUserName(request.getUserName())
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(existing -> {
                        throw new AppException(ErrorCode.USERNAME_EXISTED);
                    });
            user.setUserName(request.getUserName());
        }

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

}
