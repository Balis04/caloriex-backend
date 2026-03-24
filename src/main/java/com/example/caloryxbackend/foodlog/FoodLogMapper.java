package com.example.caloryxbackend.foodlog;

import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.foodlog.payload.request.FoodLogRequest;
import com.example.caloryxbackend.foodlog.payload.response.FoodLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface FoodLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FoodLog toEntity(FoodLogRequest request, User user);

    @Mapping(target = "userId", source = "user.id")
    FoodLogResponse toResponse(FoodLog entity);
}
