package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.CreateInvoiceRequest;
import com.hostelmanagersystem.dto.request.UpdatePaymentStatusRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;

public interface InvoiceService {
    InvoiceResponse createInvoice(String landlordId, CreateInvoiceRequest request);
    InvoiceResponse updatePaymentStatus(UpdatePaymentStatusRequest request);
    double getMonthlyRevenue(String landlordId, String month);
}
