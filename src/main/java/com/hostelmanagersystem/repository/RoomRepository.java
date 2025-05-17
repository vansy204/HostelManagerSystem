package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String>{

    Optional<Room> findByRoomNumber(String roomNumber);
feature/add-admin-service
    List<Room> findAllByStatus(RoomStatus status);

}
