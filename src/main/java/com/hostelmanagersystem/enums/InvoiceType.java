package com.hostelmanagersystem.enums;

public enum InvoiceType {
    RENT,       // Hóa đơn tiền phòng
    UTILITY,    // Hóa đơn tiền điện nước + dịch vụ
    ALL         // Gộp cả RENT + UTILITY (nếu sau này bạn muốn có hóa đơn tổng)
}
