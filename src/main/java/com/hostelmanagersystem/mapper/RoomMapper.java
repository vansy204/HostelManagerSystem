package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.entity.manager.Room;


import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomCreationRequest roomCreationRequest);
    void updateRoom(@MappingTarget Room room, RoomUpdateRequest roomUpdateRequest);
    RoomResponse toRoomResponse(Room room);
    List<RoomResponse> toResponseList(List<Room> rooms);
}




