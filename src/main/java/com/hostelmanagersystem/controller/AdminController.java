package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.annotation.LogActivity;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.mapper.UserMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.service.AdminService;
import com.hostelmanagersystem.service.ExcelExportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService adminService;
    ExcelExportService excelExportService;
    RoomRepository roomRepository;

    @GetMapping("/users")
    @LogActivity(action = "GET_ALL_USER", description = "get all users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        var result = adminService.getAllUsers();
        return ApiResponse.<List<UserResponse>>builder()
                .result(result)
                .build();
    }
    @GetMapping("/users/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        var result = adminService.getUserById(id);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }
    @DeleteMapping("/users/{id}")
    public ApiResponse<String> deleteUserById(@PathVariable String id){
        var result = adminService.deleteUserById(id);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }
    @PutMapping("/users/ban/{id}")
    public ApiResponse<String> banUserById(@PathVariable String id){
        var result = adminService.banUserById(id);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }
    @PutMapping("/users/un-ban/{id}")
    public ApiResponse<String> unBanUserById(@PathVariable String id){
        var result = adminService.unbanUserById(id);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    @PutMapping("/room/accept/{roomId}")
    public ApiResponse<String> acceptRoomCreate(@PathVariable String roomId){
        var result = adminService.acceptRoomCreateRequest(roomId);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }
    @PutMapping("/room/reject/{roomId}")
    public ApiResponse<String> rejectRoomCreate(@PathVariable String roomId){
        var result = adminService.rejectRoomCreateRequest(roomId);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }
    @GetMapping("/room/pending")
    public ApiResponse<List<RoomResponse>> getAllPendingRoom(){
        var result = adminService.getAllPendingRoomRequests();
        return ApiResponse.<List<RoomResponse>>builder()
                .result(result)
                .build();
    }
    @GetMapping("/rooms/invoice")
    public ApiResponse<List<InvoiceResponse>> getAllInvoice(){
        var result = adminService.getAllInvoice();
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(result)
                .build();
    }
    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUsers() {
        try{
            List<UserResponse> users = adminService.getAllUsers();
            byte[] excelData = excelExportService.exportUsersToExcel(users);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "bao-cao-nguoi-dung" + LocalDateTime.now() + ".xlsx");

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        }catch (IOException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/export/rooms")
    public ResponseEntity<byte[]> exportRooms() {
        try{
            List<Room> rooms= roomRepository.findAll();
            byte[] excelData = excelExportService.exportRoomsToExcel(rooms);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "bao-cao-thong-tin-phong" + LocalDateTime.now() + ".xlsx");
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        }catch (IOException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users/search/{firstName}")
    public ApiResponse<List<UserResponse>> searchUsers(@PathVariable String firstName) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(adminService.getAllUserByFirstNameContaining(firstName))
                .build();
    }
    @GetMapping("/rooms/search/{roomNumber}")
    public ApiResponse<List<RoomResponse>> searchRooms(@PathVariable String roomNumber) {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(adminService.getAllRoomByRoomNumberContaining(roomNumber))
                .build();
    }
}
