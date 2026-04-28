package com.example.caloriexbackend.customfood.mapper;

import com.example.caloriexbackend.customfood.payload.CustomFoodRequest;
import com.example.caloriexbackend.customfood.payload.CustomFoodResponse;
import com.example.caloriexbackend.entities.CustomFood;
import com.example.caloriexbackend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomFoodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    CustomFood toEntity(CustomFoodRequest request, User user);

    CustomFoodResponse toResponse(CustomFood entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    void updateEntity(@MappingTarget CustomFood entity, CustomFoodRequest request, User user);

    List<CustomFoodResponse> toResponseList(List<CustomFood> entities);
}
