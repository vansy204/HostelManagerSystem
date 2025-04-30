package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String>, RoomCustomRepository {

    Optional<Room> findByRoomNumber(String roomNumber);
}
