package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomFilterRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.RoomMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor

public class RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;
    private MongoTemplate mongoTemplate;

    @PreAuthorize("hasRole('OWNER')")

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

    public List<Room> filterRooms(RoomFilterRequest request) {
        Criteria criteria = new Criteria();

        List<Criteria> criteriaList = new ArrayList<>();


        if (request.getMinPrice() != null) {
            criteriaList.add(Criteria.where("price").gte(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            criteriaList.add(Criteria.where("price").lte(request.getMaxPrice()));
        }

        if (request.getMinSize() != null) {
            criteriaList.add(Criteria.where("area").gte(request.getMinSize()));
        }
        if (request.getMaxSize() != null) {
            criteriaList.add(Criteria.where("area").lte(request.getMaxSize()));
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            criteriaList.add(Criteria.where("status").is(request.getStatus()));
        }

        if (request.getRoomType() != null && !request.getRoomType().isBlank()) {
            criteriaList.add(Criteria.where("roomType").is(request.getRoomType()));
        }

        if (request.getFacilities() != null && !request.getFacilities().isEmpty()) {
            criteriaList.add(Criteria.where("facilities").all(request.getFacilities()));
        }

        if (request.getLeaseTerm() != null) {
            criteriaList.add(Criteria.where("leaseTerm").is(request.getLeaseTerm()));
        }

        if (request.getCondition() != null && !request.getCondition().isBlank()) {
            criteriaList.add(Criteria.where("condition").is(request.getCondition()));
        }

        if (request.getProvince() != null && !request.getProvince().isBlank()) {
            criteriaList.add(Criteria.where("province").is(request.getProvince()));
        }

        if (request.getDistrict() != null && !request.getDistrict().isBlank()) {
            criteriaList.add(Criteria.where("district").is(request.getDistrict()));
        }

        if (request.getWard() != null && !request.getWard().isBlank()) {
            criteriaList.add(Criteria.where("ward").is(request.getWard()));
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            criteriaList.add(Criteria.where("addressText").regex(".*" + request.getKeyword() + ".*", "i"));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Room.class);

    }

    @PreAuthorize("hasRole('OWNER')")
    public Room updateRoom(String roomId, RoomUpdateRequest roomUpdateRequest) {
        Room oldRoom = findRoomById(roomId);
        roomMapper.updateRoom(oldRoom,roomUpdateRequest);
        return roomRepository.save(oldRoom);
    }


    @PreAuthorize("hasRole('OWNER')")
    public String deleteRoom(String roomId) {
        Room oldRoom = findRoomById(roomId);
        roomRepository.delete(oldRoom);
        return "Room deleted successfully";
    }

}
