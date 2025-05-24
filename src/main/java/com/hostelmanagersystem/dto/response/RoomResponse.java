package com.hostelmanagersystem.dto.response;

import lombok.*; import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    String id;
    String roomNumber;
    String roomSize;
    Double price;
    String status;
    String description;
    String roomType;
    List<String> facilities;
    String rentalTime;
    String condition;
    Integer floor;
    List<String> mediaUrls;

    String province;
    String district;
    String ward;
    String addressText;
}