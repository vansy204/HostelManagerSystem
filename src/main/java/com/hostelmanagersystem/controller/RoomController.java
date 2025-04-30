package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

    @PostMapping("/")
    public ApiResponse<Room> createRoom(@RequestBody RoomCreationRequest room) {

        return ApiResponse.<Room>builder()
                .result(roomService.createRoom(room))
                .build();
    }

    @GetMapping("/filter")
    public ApiResponse<List<Room>> filterRooms(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minSize,
            @RequestParam(required = false) Double maxSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) List<String> facilities,
            @RequestParam(required = false) Integer leaseTerm,
            @RequestParam(required = false) String condition) {

        var result = roomService.filterRooms(minPrice, maxPrice, minSize, maxSize, status, roomType, facilities, leaseTerm, condition);
        return ApiResponse.<List<Room>>builder().result(result).build();
    }

    @GetMapping("/")
    public ApiResponse<List<Room>> getAllRooms() {
        var result = roomService.findAll();
        return ApiResponse.<List<Room>>builder()
                .result(result)
                .build();
    }
    @GetMapping("/numbers/{roomNumber}")
    public ApiResponse<Room> getRoomByNumber(@PathVariable String roomNumber) {
        return ApiResponse.<Room>builder()
                .result(roomService.findRoomByRoomNumber(roomNumber))
                .build();
    }
    @GetMapping("/{roomId}")
    public ApiResponse<Room> getRoomById(@PathVariable String roomId) {
        var result = roomService.findRoomById(roomId);
        return ApiResponse.<Room>builder()
                .result(result)
                .build();
    }
    @PutMapping("/{roomId}")
    public ApiResponse<Room> updateRoom(@PathVariable String roomId,
                                        @RequestBody RoomUpdateRequest roomUpdateRequest) {
        var result = roomService.updateRoom(roomId, roomUpdateRequest);
        return ApiResponse.<Room>builder()
                .result(result)
                .build();
    }
    @DeleteMapping("/{roomId}")
    public ApiResponse<String> deleteRoom(@PathVariable String roomId) {
        var result = roomService.deleteRoom(roomId);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }
}
