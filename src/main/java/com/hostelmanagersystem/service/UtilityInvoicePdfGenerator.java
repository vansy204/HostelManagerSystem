package com.hostelmanagersystem.service;

import com.hostelmanagersystem.entity.manager.Invoice;
import com.hostelmanagersystem.entity.manager.UtilityUsage;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.bcel.Utility;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class UtilityInvoicePdfGenerator {
    public static byte[] generateUtilityInvoicePdf(Invoice invoice, UtilityUsage utility) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("UTILITY INVOICE"));
            document.add(new Paragraph("Month: " + invoice.getCreatedAt().getMonth()));
            document.add(new Paragraph("Room ID: " + invoice.getRoomId()));
            document.add(new Paragraph("Tenant ID: " + invoice.getTenantId()));
            document.add(new Paragraph("Electricity Usage (kWh): " + utility.getNewElectricity()));
            document.add(new Paragraph("Water Usage (m³): " + utility.getNewWater()));
//            document.add(new Paragraph("Service Fee: " + invoice.getServiceFee()));
            document.add(new Paragraph("Electricity Fee: " + invoice.getElectricityAmount()));
            document.add(new Paragraph("Water Fee: " + invoice.getWaterAmount()));
            document.add(new Paragraph("Total Amount: " + invoice.getTotalAmount()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating utility invoice PDF", e);
        }
    }
}
