package com.hostelmanagersystem.service;

import com.hostelmanagersystem.entity.manager.Invoice;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoicePdfGenerator {
    public static byte[] generateInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("INVOICE"));
            document.add(new Paragraph("Month: " + invoice.getMonth()));
            document.add(new Paragraph("Room: " + invoice.getRoomId()));
            document.add(new Paragraph("Tenant ID: " + invoice.getTenantId()));
            document.add(new Paragraph("Rent: " + invoice.getRentAmount()));
            document.add(new Paragraph("Electricity: " + invoice.getElectricityAmount()));
            document.add(new Paragraph("Water: " + invoice.getWaterAmount()));
            document.add(new Paragraph("Service: " + invoice.getServiceAmount()));
            document.add(new Paragraph("Total: " + invoice.getTotalAmount()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
