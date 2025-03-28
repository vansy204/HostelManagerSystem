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
public class RoomCreationRequest {
    String roomNumber;
    String roomSize;
    Double price;
    RoomStatus status;
    String description;
    List<String> mediaUrls;
}
