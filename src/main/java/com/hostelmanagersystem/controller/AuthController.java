package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.AuthRequest;
import com.hostelmanagersystem.dto.request.IntrospectRequest;
import com.hostelmanagersystem.dto.request.LogoutRequest;
import com.hostelmanagersystem.dto.request.RefreshRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.AuthResponse;
import com.hostelmanagersystem.dto.response.IntrospectResponse;
import com.hostelmanagersystem.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthResponse> authenticate(@RequestBody AuthRequest authRequest){
        var result = authenticationService.authenticate(authRequest);
        return ApiResponse.<AuthResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspecRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspecRequest);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthResponse> refresh(@RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(refreshRequest);
        return ApiResponse.<AuthResponse>builder().result(result).build();
    }

}
