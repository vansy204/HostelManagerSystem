package com.hostelmanagersystem.enums;

public enum RoomStatus {
    AVAILABLE, //Phòng sẵn sàng cho thuê
    OCCUPIED, // Đang có người thuê
    RESERVED, // Đã có người đặt cọc hoặc giưx chỗ
    MAINTENANCE, // Đang bảo trì
    CLEANING, //Đang dọn dẹp sau khi trả phòng
    PENDING, //Chờ duyệt bài đăng
    UNAVAILABLE //Tạm ngừng hoạt động
}