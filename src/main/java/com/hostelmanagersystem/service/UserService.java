package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.CreateUserRequest;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.enums.RoleEnum;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
        var role = roleRepository.findById("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.setRole(role);

        user.setCreateAt(Instant.now());
        try{
            userRepository.save(user);
        }catch (DataIntegrityViolationException ex){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }
}
