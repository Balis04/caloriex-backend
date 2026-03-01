package com.example.caloryxbackend.food_log;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.food_log.payload.FoodLodRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FoodLogService {

    private final CurrentUserService currentUserService;
    private final FoodLogRepository repository;

    public FoodLog createFoodLog(FoodLodRequest request) {
        FoodLog entity = new FoodLog();

        entity.setFoodName(request.getFoodName());
        entity.setMealTime(request.getMealTime());
        entity.setAmount(request.getAmount());
        entity.setUnit(request.getUnit());
        entity.setCalories(request.getCalories());
        entity.setProtein(request.getProtein());
        entity.setCarbohydrates(request.getCarbohydrates());
        entity.setFat(request.getFat());

        entity.setConsumedAt(request.getConsumedAt() != null ? request.getConsumedAt() : LocalDateTime.now());

        entity.setAuth0Id(currentUserService.getAuth0Id());

        return repository.save(entity);
    }
}
