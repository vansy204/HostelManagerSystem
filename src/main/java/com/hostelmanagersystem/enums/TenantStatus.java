package com.hostelmanagersystem.enums;

public enum TenantStatus {
    PENDING,     // yêu cầu vừa gửi
    APPROVED,    // chủ trọ chấp nhận
    REJECTED,    // chủ trọ từ chối
    CANCELLED,   // người thuê hủy
    ACTIVE,      // đang thuê
    INACTIVE,    // ngưng thuê
    MOVED_OUT    // đã chuyển đi
}
