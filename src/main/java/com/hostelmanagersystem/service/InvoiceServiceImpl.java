package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Invoice;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.InvoiceStatus;
import com.hostelmanagersystem.mapper.InvoiceMapper;
import com.hostelmanagersystem.repository.InvoiceRepository;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    private final RoomRepository roomRepository;
//    TenantRepository tenantRepository;


    @Override
    public InvoiceResponse createInvoice(String ownerId, InvoiceCreateRequest request) {
        Invoice invoice = invoiceMapper.toInvoice(request, ownerId);
        invoice.setTotalAmount(request.getRentAmount() + request.getElectricityAmount() +
                request.getWaterAmount() + request.getServiceAmount());
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse getInvoiceById(String ownerId, String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .filter(inv -> inv.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Invoice not found or access denied"));
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByOwnerAndMonth(String ownerId, String month) {
        List<Invoice> invoices = invoiceRepository.findByOwnerIdAndMonth(ownerId, month);
        return invoices.stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    public List<InvoiceResponse> getInvoicesByTenant(String tenantId) {
        List<Invoice> invoices = invoiceRepository.findByTenantId(tenantId);
        return invoices.stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    public InvoiceResponse getInvoiceByTenant(String tenantId) {
        Invoice invoice = invoiceRepository.findInvoiceByTenantId(tenantId);
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    @Override
    public InvoiceResponse updatePaymentStatus(String ownerId, String invoiceId, InvoiceStatus status, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .filter(inv -> inv.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Invoice not found or access denied"));

        invoice.setStatus(status);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentDate(LocalDateTime.now());

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(saved);
    }

    @Override
    public void sendInvoiceEmailToTenant(String ownerId, String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .filter(inv -> inv.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Lấy email tenant
        User tenant = userRepository.findById(invoice.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        String email = tenant.getEmail();

        byte[] pdf = InvoicePdfGenerator.generateInvoicePdf(invoice);

        emailService.sendInvoiceEmail(
                email,
                "Hóa đơn phòng trọ tháng " + invoice.getMonth(),
                "Xin chào, vui lòng xem hóa đơn trong file đính kèm.",
                pdf,
                "invoice-" + invoice.getMonth() + ".pdf"
        );
    }
}
