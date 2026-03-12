package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.customfood.payload.CustomFoodRequest;
import com.example.caloryxbackend.customfood.payload.CustomFoodResponse;
import com.example.caloryxbackend.entities.CustomFood;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomFoodService {

    private final CustomFoodRepository customFoodRepository;
    private final CurrentUserService currentUserService;

    public CustomFoodResponse create(CustomFoodRequest request) {
        String auth0Id = currentUserService.getAuth0Id();

        CustomFood entity = new CustomFood();
        entity.setAuth0Id(auth0Id);
        applyRequest(entity, request);

        return map(customFoodRepository.save(entity));
    }

    public CustomFoodResponse update(UUID id, CustomFoodRequest request) {
        String auth0Id = currentUserService.getAuth0Id();

        CustomFood entity = customFoodRepository.findByIdAndAuth0Id(id, auth0Id)
                .orElseThrow(() -> new NotFoundException("Custom food not found"));

        applyRequest(entity, request);

        return map(customFoodRepository.save(entity));
    }

    public void delete(UUID id) {
        String auth0Id = currentUserService.getAuth0Id();

        CustomFood entity = customFoodRepository.findByIdAndAuth0Id(id, auth0Id)
                .orElseThrow(() -> new NotFoundException("Custom food not found"));

        customFoodRepository.delete(entity);
    }

    public List<CustomFoodResponse> getAll() {
        return customFoodRepository.findAll().stream()
                .map(this::map)
                .toList();
    }

    public List<CustomFoodResponse> getMine() {
        String auth0Id = currentUserService.getAuth0Id();

        return customFoodRepository.findAllByAuth0Id(auth0Id).stream()
                .map(this::map)
                .toList();
    }

    public List<CustomFoodResponse> getNotMine() {
        String auth0Id = currentUserService.getAuth0Id();

        return customFoodRepository.findAllNotOwnedBy(auth0Id).stream()
                .map(this::map)
                .toList();
    }

    private void applyRequest(CustomFood entity, CustomFoodRequest request) {
        entity.setName(request.getName());
        entity.setCalories(request.getCalories());
        entity.setFat(request.getFat());
        entity.setCarbohydrates(request.getCarbohydrates());
    }

    private CustomFoodResponse map(CustomFood entity) {
        return new CustomFoodResponse(
                entity.getId(),
                entity.getName(),
                entity.getCalories(),
                entity.getFat(),
                entity.getCarbohydrates(),
                entity.getAuth0Id()
        );
    }
}
