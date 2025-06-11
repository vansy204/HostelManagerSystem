package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageUpdateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;
import com.hostelmanagersystem.enums.InvoiceStatus;

import java.util.List;

public interface UtilityService {

    // ===== UTILITY USAGE =====
    UtilityUsageResponse createUtilityUsage(String ownerId, UtilityUsageCreateRequest request);

    UtilityUsageResponse getUtilityUsageDetail(String ownerId, String usageId);

    List<UtilityUsageResponse> getUtilityUsagesByMonth(String ownerId, String month); // format: yyyy-MM

    UtilityUsageResponse updateUtilityUsage(String ownerId, String usageId, UtilityUsageUpdateRequest request);

    void deleteUtilityUsage(String ownerId, String usageId);


    // ===== UTILITY CONFIG =====
    UtilityConfigResponse getConfigByOwnerId(String ownerId);

    UtilityConfigResponse updateConfig(String ownerId, UtilityConfigUpdateRequest request);
}

