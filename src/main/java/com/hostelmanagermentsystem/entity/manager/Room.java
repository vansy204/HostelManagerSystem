package com.hostelmanagermentsystem.entity.manager;

import com.hostelmanagermentsystem.enums.RoomStatus;
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
    Double roomSize;
    String buildingId;
    Double price;
    RoomStatus status;
    String description;
    List<String> mediaUrls;
}
