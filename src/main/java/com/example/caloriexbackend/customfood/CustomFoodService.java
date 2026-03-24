package com.example.caloriexbackend.customfood;

import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.customfood.payload.CustomFoodRequest;
import com.example.caloriexbackend.customfood.payload.CustomFoodResponse;
import com.example.caloriexbackend.customfood.repository.CustomFoodRepository;
import com.example.caloriexbackend.entities.CustomFood;
import com.example.caloriexbackend.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomFoodService {

    private final CustomFoodRepository customFoodRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final CustomFoodMapper customFoodMapper;

    public CustomFoodResponse create(CustomFoodRequest request) {
        User user = authenticatedUserService.getUser();

        CustomFood customFood = customFoodMapper.toEntity(request, user);

        return customFoodMapper.toResponse(
                customFoodRepository.save(customFood)
        );
    }

    public CustomFoodResponse update(UUID id, CustomFoodRequest request) {
        User user = authenticatedUserService.getUser();

        CustomFood customFood = findCustomFood(user, id);

        customFoodMapper.updateEntity(customFood, request, user);

        return customFoodMapper.toResponse(
                customFoodRepository.save(customFood)
        );
    }

    public void delete(UUID id) {
        User user = authenticatedUserService.getUser();

        CustomFood entity = findCustomFood(user, id);

        customFoodRepository.delete(entity);
    }

    public List<CustomFoodResponse> getAll() {
        return customFoodMapper.toResponseList(customFoodRepository.findAll());
    }

    public List<CustomFoodResponse> getMine() {
        User user = authenticatedUserService.getUser();

        return customFoodMapper.toResponseList(customFoodRepository.findAllByUser(user));
    }

    public List<CustomFoodResponse> getNotMine() {
        User user = authenticatedUserService.getUser();

        return customFoodMapper.toResponseList(customFoodRepository.findAllNotOwnedBy(user));
    }

    private CustomFood findCustomFood(User user, UUID id){
        return customFoodRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Custom food not found"));
    }
}
