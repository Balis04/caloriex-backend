package com.example.caloryxbackend.user;

import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.user.payload.request.RegisterRequest;
import com.example.caloryxbackend.user.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auth0id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "coachProfile", ignore = true)
    void updateForRegistration(RegisterRequest request, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auth0id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "coachProfile", ignore = true)
    void updateFromRequest(RegisterRequest request, @MappingTarget User entity);
}
