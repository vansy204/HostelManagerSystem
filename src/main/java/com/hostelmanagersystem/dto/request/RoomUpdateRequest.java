package com.hostelmanagersystem.dto.request;

import com.hostelmanagersystem.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomUpdateRequest {
    String id;
    String roomNumber;
    String roomSize;   // Diện tích
    Double price;      // Giá
    RoomStatus status; // Trạng thái phòng
    String roomType;   // Loại phòng
    List<String> facilities; // Tiện ích
    Integer leaseTerm; // Thời gian thuê
    String condition;  // Tình trạng phòng
    String description;
    List<String> mediaUrls;
}
