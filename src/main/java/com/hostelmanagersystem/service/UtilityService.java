package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityInvoiceCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityInvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;

import java.util.List;

public interface UtilityService {
    UtilityUsageResponse createUtilityUsage(String landlordId, UtilityUsageCreateRequest request);
    List<UtilityUsageResponse> getUtilityUsagesByMonth(String landlordId, String month);

    // Tạo hóa đơn tiện ích dựa trên chỉ số đã nhập, tính tiền dựa trên đơn giá config
    UtilityInvoiceResponse createUtilityInvoice(String landlordId, UtilityInvoiceCreateRequest request);

    // Lấy hóa đơn tiện ích theo landlord và tháng
    List<UtilityInvoiceResponse> getUtilityInvoicesByMonth(String landlordId, String month);

    UtilityConfigResponse getConfigByLandlordId(String landlordId);

    UtilityConfigResponse updateConfig(String landlordId, UtilityConfigUpdateRequest request);
}
