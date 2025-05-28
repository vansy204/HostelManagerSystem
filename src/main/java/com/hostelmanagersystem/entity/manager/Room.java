package com.hostelmanagersystem.entity.manager;

import com.hostelmanagersystem.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(value = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room {
    @MongoId
    String id;
    String roomNumber;
    String ownerId;

    String roomSize;   // Diện tích
    Double price;      // Giá
    RoomStatus status; // Trạng thái phòng
    String roomType;   // Loại phòng
    List<String> facilities; // Tiện ích
    Integer leaseTerm; // Thời gian thuê
    String condition;  // Tình trạng phòng
    Integer floor;
    String description;
    List<String> mediaUrls;

    String province;   // Tỉnh/Thành phố
    String district;   // Quận/Huyện
    String ward;       // Phường/Xã
    String addressText;
}
