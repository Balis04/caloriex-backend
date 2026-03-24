package com.example.caloriexbackend.foodlog.mapper;

import com.example.caloriexbackend.entities.FoodLog;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.foodlog.payload.request.FoodLogRequest;
import com.example.caloriexbackend.foodlog.payload.response.FoodLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
