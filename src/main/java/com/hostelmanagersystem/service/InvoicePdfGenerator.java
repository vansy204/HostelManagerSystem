package com.hostelmanagersystem.service;

import com.hostelmanagersystem.entity.manager.Invoice;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoicePdfGenerator {

    public byte[] generatePdf(Invoice invoice) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("HÓA ĐƠN THANH TOÁN"));
        doc.add(new Paragraph("Phòng: " + invoice.getRoomId()));
        doc.add(new Paragraph("Tháng: " + invoice.getMonth()));
        doc.add(new Paragraph("Tiền phòng: " + invoice.getRentAmount()));
        doc.add(new Paragraph("Tiền điện: " + invoice.getElectricityAmount()));
        doc.add(new Paragraph("Tiền nước: " + invoice.getWaterAmount()));
        doc.add(new Paragraph("Dịch vụ khác: " + invoice.getServiceAmount()));
        doc.add(new Paragraph("TỔNG CỘNG: " + invoice.getTotalAmount()));
        doc.close();

        return baos.toByteArray();
    }
}
