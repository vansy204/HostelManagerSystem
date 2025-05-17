package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.NotificationCreateRequest;
import com.hostelmanagersystem.dto.response.NotificationResponse;
import com.hostelmanagersystem.entity.manager.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toEntity(NotificationCreateRequest request, String senderId, String recipientId);

    NotificationResponse toDto(Notification notification);
}
