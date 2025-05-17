package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.TenantCreationRequest;
import com.hostelmanagersystem.dto.request.TenantUpdateRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.TenantMapper;
import com.hostelmanagersystem.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TenantService {
    TenantRepository tenantRepository;
    TenantMapper tenantMapper;

    public TenantResponse createTenant(String landlordId, TenantCreationRequest request) {
        Tenant tenant = tenantMapper.toTenant(request);
        tenant.setLandlordId(landlordId);

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    public List<TenantResponse> getTenantsByLandlord(String landlordId) {
        return tenantRepository.findByLandlordId(landlordId)
                .stream()
                .map(tenantMapper::toTenantResponse)
                .collect(Collectors.toList());
    }

    public TenantResponse updateTenant(String tenantId, TenantUpdateRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        tenantMapper.updateTenant(tenant, request);
        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    public String deleteTenant(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        tenantRepository.delete(tenant);
        return "Tenant deleted successfully";
    }
}
