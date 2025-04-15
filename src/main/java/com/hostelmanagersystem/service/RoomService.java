package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.RoomMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor

public class RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public Room createRoom(RoomCreationRequest room) {
        var roomCreate = roomMapper.toRoom(room);
        var existingRoom = roomRepository.findByRoomNumber(room.getRoomNumber());
        if (existingRoom.isPresent()) {
            throw new AppException(ErrorCode.ROOM_EXISTED);
        }
       try{
           return roomRepository.save(roomCreate);
       }catch (DataIntegrityViolationException ex){
           throw new AppException(ErrorCode.ROOM_EXISTED);
       }
    }
    public Room findRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() ->new AppException(ErrorCode.ROOM_NOT_EXISTED));
    }
    public Room findRoomByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() ->new AppException(ErrorCode.ROOM_NOT_EXISTED));
    }
    public List<Room> findAll() {
        return roomRepository.findAll();
    }
    public Room updateRoom(String roomId, RoomUpdateRequest roomUpdateRequest) {
        Room oldRoom = findRoomById(roomId);
        roomMapper.updateRoom(oldRoom,roomUpdateRequest);
        return roomRepository.save(oldRoom);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRoom(String roomId) {
        Room oldRoom = findRoomById(roomId);
        roomRepository.delete(oldRoom);
        return "Room deleted successfully";
    }

}
