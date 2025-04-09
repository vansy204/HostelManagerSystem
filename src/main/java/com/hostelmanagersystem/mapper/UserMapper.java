package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.CreateUserRequest;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.identity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
    User toUser(CreateUserRequest createUserRequest);
}
