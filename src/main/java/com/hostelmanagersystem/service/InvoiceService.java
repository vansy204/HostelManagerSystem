package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(String ownerId, InvoiceCreateRequest request);

    InvoiceResponse getInvoiceById(String ownerId, String invoiceId);

    List<InvoiceResponse> getInvoicesByOwnerAndMonth(String ownerId, String month);

    List<InvoiceResponse> getInvoicesByTenant(String tenantId);
    InvoiceResponse getInvoiceByTenant(String tenantId);
    InvoiceResponse updatePaymentStatus(String ownerId, String invoiceId, InvoiceStatus status, String paymentMethod);

    void sendInvoiceEmailToTenant(String ownerId, String invoiceId);
}
