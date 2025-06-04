package com.hostelmanagersystem.enums;

public enum ContractStatus {
    PENDING,            // Đang chờ bắt đầu (tạo xong nhưng chưa đến ngày hiệu lực)
    ACTIVE,             // Đang hiệu lực (startDate <= hôm nay <= endDate)
    EXPIRING_SOON,      // Sắp hết hạn (ví dụ còn <= 7 ngày)
    EXPIRED,            // Hết hạn (hôm nay > endDate)
    TERMINATED,         // Đã chấm dứt sớm (chủ động kết thúc hợp đồng)
    CANCELLED           // Đã huỷ trước khi có hiệu lực
}
