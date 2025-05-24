package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.service.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService adminService;

    @GetMapping("/users")
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

}
