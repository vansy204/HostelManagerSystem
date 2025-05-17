package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.CreateInvoiceRequest;
import com.hostelmanagersystem.dto.request.UpdatePaymentStatusRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Invoice;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.InvoiceStatus;
import com.hostelmanagersystem.mapper.InvoiceMapper;
import com.hostelmanagersystem.repository.InvoiceRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService{
    InvoiceRepository invoiceRepository;
    InvoiceMapper invoiceMapper;
    InvoicePdfGenerator pdfGenerator;
    EmailService emailService;
    UserRepository userRepository;

    @Override
    public InvoiceResponse createInvoice(String landlordId, CreateInvoiceRequest request) {
        // Kiểm tra xem đã có hóa đơn tháng đó chưa
        Optional<Invoice> existing = invoiceRepository.findByTenantIdAndMonth(request.getTenantId(), request.getMonth());
        if (existing.isPresent()) {
            throw new RuntimeException("Invoice already exists for this tenant and month");
        }

        Invoice invoice = invoiceMapper.toEntity(request);
        invoice.setLandlordId(landlordId);
        invoice.setTotalAmount(request.getRentAmount() + request.getElectricityAmount()
                + request.getWaterAmount() + request.getServiceAmount());
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCreatedAt(LocalDateTime.now());
//        invoice.setElectronicCode("HD" + request.getMonth().replace("-", "") + "-R" + roomIdSuffix);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    public void sendInvoiceToTenant(String invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // (Giả sử đã có tenantRepository)
        User tenant = userRepository.findById(invoice.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        byte[] pdf = pdfGenerator.generatePdf(invoice);
        emailService.sendInvoiceEmail(tenant.getEmail(), pdf, invoice.getMonth());
    }

    public InvoiceResponse updatePaymentStatus(UpdatePaymentStatusRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(request.getStatus());
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setPaymentDate(request.getPaymentDate());

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    public double getMonthlyRevenue(String landlordId, String month) {
        List<Invoice> invoices = invoiceRepository.findByLandlordIdAndMonth(landlordId, month);
        return invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

}
