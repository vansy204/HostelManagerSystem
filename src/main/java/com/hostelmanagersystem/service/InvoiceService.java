package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(String landlordId, InvoiceCreateRequest request);

    InvoiceResponse getInvoiceById(String landlordId, String invoiceId);

    List<InvoiceResponse> getInvoicesByLandlordAndMonth(String landlordId, String month);

    List<InvoiceResponse> getInvoicesByTenant(String tenantId);

    InvoiceResponse updatePaymentStatus(String landlordId, String invoiceId, InvoiceStatus status, String paymentMethod);

    void sendInvoiceEmailToTenant(String landlordId, String invoiceId);
}
