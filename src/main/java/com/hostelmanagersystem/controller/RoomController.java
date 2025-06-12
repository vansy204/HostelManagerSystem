package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomFilterRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.mapper.RoomMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    @PostMapping("/")
    public ApiResponse<String> createRoom(@RequestBody RoomCreationRequest room) {
        var result = roomService.createRoom(room);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    @PostMapping("/filter")
    public List<Room> filterRooms(@RequestBody RoomFilterRequest request) {
        return roomService.filterRooms(request);
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
    @GetMapping("/availableRoom")
    public List<RoomResponse> getAllAvailableRoom(){
        return roomRepository.findAllByStatus(RoomStatus.AVAILABLE)
                .stream().map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }

}
