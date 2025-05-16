package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String>{
    List<Room> findAllByStatusNotContainsIgnoreCase(String status);
    Optional<Room> findByRoomNumber(String roomNumber);

    Optional<Room> findById(String id);
}
