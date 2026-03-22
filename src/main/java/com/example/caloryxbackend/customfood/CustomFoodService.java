package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.customfood.payload.CustomFoodRequest;
import com.example.caloryxbackend.customfood.payload.CustomFoodResponse;
import com.example.caloryxbackend.entities.CustomFood;
import com.example.caloryxbackend.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomFoodService {

    private final CustomFoodRepository customFoodRepository;
    private final CurrentUserService currentUserService;
    private final CustomFoodMapper customFoodMapper;

    public CustomFoodResponse create(CustomFoodRequest request) {
        User user = currentUserService.getUser();

        CustomFood customFood = customFoodMapper.toEntity(request, user);

        return customFoodMapper.toResponse(
                customFoodRepository.save(customFood)
        );
    }

    public CustomFoodResponse update(UUID id, CustomFoodRequest request) {
        User user = currentUserService.getUser();

        CustomFood customFood = findCustomFood(user, id);

        customFoodMapper.updateEntity(customFood, request, user);

        return customFoodMapper.toResponse(
                customFoodRepository.save(customFood)
        );
    }

    public void delete(UUID id) {
        User user = currentUserService.getUser();

        CustomFood entity = findCustomFood(user, id);

        customFoodRepository.delete(entity);
    }

    public List<CustomFoodResponse> getAll() {
        return customFoodMapper.toResponseList(customFoodRepository.findAll());
    }

    public List<CustomFoodResponse> getMine() {
        User user = currentUserService.getUser();

        return customFoodMapper.toResponseList(customFoodRepository.findAllByUser(user));
    }

    public List<CustomFoodResponse> getNotMine() {
        User user = currentUserService.getUser();

        return customFoodMapper.toResponseList(customFoodRepository.findAllNotOwnedBy(user));
    }

    private CustomFood findCustomFood(User user, UUID id){
        return customFoodRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Custom food not found"));
    }
}
