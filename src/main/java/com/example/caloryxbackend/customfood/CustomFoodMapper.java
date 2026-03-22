package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.customfood.payload.CustomFoodRequest;
import com.example.caloryxbackend.customfood.payload.CustomFoodResponse;
import com.example.caloryxbackend.entities.CustomFood;
import com.example.caloryxbackend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomFoodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    CustomFood toEntity(CustomFoodRequest request, User user);

    CustomFoodResponse toResponse(CustomFood entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    void updateEntity(@MappingTarget CustomFood entity, CustomFoodRequest request, User user);

    List<CustomFoodResponse> toResponseList(List<CustomFood> entities);
}
