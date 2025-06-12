package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.CreateUserRequest;
import com.hostelmanagersystem.dto.request.UpdateUserRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)

public class UserController {
    UserService userService;

    @PostMapping("/registration")
    public ApiResponse<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        var result = userService.createUser(createUserRequest);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getCurrentUser())
                .build();
    }
    @PutMapping("/update")
    public ApiResponse<UserResponse> updateUser(@RequestBody UpdateUserRequest request) {
        var result = userService.updateUser(request);
        return ApiResponse.<UserResponse>builder().result(result).build();
    }
    @GetMapping("/user/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }
}
