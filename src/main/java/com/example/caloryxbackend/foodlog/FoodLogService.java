package com.example.caloryxbackend.foodlog;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.foodlog.payload.FoodLogAmountUpdateRequest;
import com.example.caloryxbackend.foodlog.payload.FoodLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodLogService {

    private final CurrentUserService currentUserService;
    private final FoodLogRepository repository;

    public FoodLog createFoodLog(FoodLogRequest request) {
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

    public FoodLog updateFoodLogAmount(UUID foodLogId, FoodLogAmountUpdateRequest request) {
        String auth0Id = currentUserService.getAuth0Id();

        FoodLog foodLog = repository.findByIdAndAuth0Id(foodLogId, auth0Id)
                .orElseThrow(() -> new NotFoundException("Food log not found"));

        Double previousAmount = foodLog.getAmount();
        if (previousAmount == null || previousAmount <= 0) {
            throw new BadRequestException("Cannot recalculate macros and calories because original amount is missing or zero");
        }

        double ratio = request.getAmount() / previousAmount;

        foodLog.setAmount(request.getAmount());
        foodLog.setCalories(scale(foodLog.getCalories(), ratio));
        foodLog.setProtein(scale(foodLog.getProtein(), ratio));
        foodLog.setCarbohydrates(scale(foodLog.getCarbohydrates(), ratio));
        foodLog.setFat(scale(foodLog.getFat(), ratio));
        foodLog.setUpdatedBy(auth0Id);

        return repository.save(foodLog);
    }

    public void deleteFoodLog(UUID foodLogId) {
        String auth0Id = currentUserService.getAuth0Id();

        FoodLog foodLog = repository.findByIdAndAuth0Id(foodLogId, auth0Id)
                .orElseThrow(() -> new NotFoundException("Food log not found"));

        repository.delete(foodLog);
    }

    private Double scale(Double value, double ratio) {
        return value == null ? null : value * ratio;
    }
}
