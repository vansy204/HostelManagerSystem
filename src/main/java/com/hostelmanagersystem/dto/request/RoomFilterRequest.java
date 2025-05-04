package com.hostelmanagersystem.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class RoomFilterRequest {
    String roomNumber;              // Lọc theo số phòng cụ thể (tùy chọn)
    Double minPrice;               // Khoảng giá
    Double maxPrice;
    Double minSize;                // Khoảng diện tích
    Double maxSize;
    String status;                 // Trạng thái phòng (AVAILABLE, OCCUPIED, MAINTENANCE)
    String roomType;              // Loại phòng
    List<String> facilities;      // Danh sách tiện ích mong muốn
    Integer leaseTerm;            // Thời gian thuê tối thiểu (tháng)
    String condition;

    String province;
    String district;
    String ward;

    String keyword;

}
