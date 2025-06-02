package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityInvoiceCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityInvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;

import java.util.List;

public interface UtilityService {
    UtilityUsageResponse createUtilityUsage(String ownerId, UtilityUsageCreateRequest request);
    List<UtilityUsageResponse> getUtilityUsagesByMonth(String ownerId, String month);

    // Tạo hóa đơn tiện ích dựa trên chỉ số đã nhập, tính tiền dựa trên đơn giá config
    UtilityInvoiceResponse createUtilityInvoice(String ownerId, UtilityInvoiceCreateRequest request);

    // Lấy hóa đơn tiện ích theo owner và tháng
    List<UtilityInvoiceResponse> getUtilityInvoicesByMonth(String ownerId, String month);

    UtilityConfigResponse getConfigByOwnerId(String ownerId);

    UtilityConfigResponse updateConfig(String ownerId, UtilityConfigUpdateRequest request);
}
