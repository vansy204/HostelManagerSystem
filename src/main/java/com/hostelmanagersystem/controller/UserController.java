package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.CreateUserRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)

public class UserController {
    UserService userService;

    @PostMapping("/registration")
    public ApiResponse<UserResponse> createUser (@RequestBody CreateUserRequest createUserRequest) {
        var result = userService.createUser(createUserRequest);
        log.info("user password {}", createUserRequest.getPassword());
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

}
