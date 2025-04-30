package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomCustomRepository {
    List<Room> findByFilters(Double minPrice,
                             Double maxPrice,
                             Double minSize,
                             Double maxSize,
                             String status,
                             String roomType,
                             List<String> facilities,
                             Integer leaseTerm,
                             String condition);
}

