package com.hostelmanagersystem.enums;

public enum TenantStatus {
    PENDING,              // Đã gửi yêu cầu, chờ duyệt
    APPROVED,             // Đã duyệt, chờ đặt cọc
    DEPOSITED,            // Đã đặt cọc, chờ ký hợp đồng
    CONTRACT_CONFIRMED,   // Đã ký hợp đồng, đang thuê
    COMPLETED,            // Đã kết thúc hợp đồng
    CANCELLED,            // Người thuê huỷ yêu cầu
    REJECTED,
    INACTIVE,             // ngưng thuê
    MOVED_OUT             // Chủ nhà từ chối yêu cầu
}

