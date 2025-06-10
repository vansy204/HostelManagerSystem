package com.hostelmanagersystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceStatisticsResponse {
    long totalInvoices;    // Tổng số hóa đơn trong khoảng thời gian
    double totalRevenue;   // Tổng doanh thu (tổng tiền đã thu)
    long paidInvoices;     // Số hóa đơn đã thanh toán
    long unpaidInvoices;   // Số hóa đơn chưa thanh toán
    long cancelledInvoices;
}
