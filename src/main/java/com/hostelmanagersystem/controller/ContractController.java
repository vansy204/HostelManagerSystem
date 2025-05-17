package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.ContractCreateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.service.ContractService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractController {
    ContractService contractService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ApiResponse<ContractResponse> createContract(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ContractCreateRequest request
    ) {
        ContractResponse response = contractService.createContract(user.getId(), request);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Tạo hợp đồng thành công")
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
    public ApiResponse<ContractResponse> getContractById(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        ContractResponse response = contractService.getContractById(id, user);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Lấy hợp đồng thành công")
                .build();
    }
}
