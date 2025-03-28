package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.RoomCreationRequest;
import com.hostelmanagersystem.dto.request.RoomUpdateRequest;
import com.hostelmanagersystem.entity.manager.Room;
import org.mapstruct.Mapper;

import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomCreationRequest roomCreationRequest);
    void updateRoom(@MappingTarget Room room, RoomUpdateRequest roomUpdateRequest);

}
