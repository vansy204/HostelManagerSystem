package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String>{
    Optional<Room> findByRoomNumber(String roomNumber);
    Optional<List<Room>> findAllByRoomNumberContainingIgnoreCase(String roomNumber);
    List<Room> findAllByStatus(RoomStatus status);
    Optional<Room> findByIdAndOwnerId(String id, String owner);
    Optional<Room> findRoomById(String id);
}
