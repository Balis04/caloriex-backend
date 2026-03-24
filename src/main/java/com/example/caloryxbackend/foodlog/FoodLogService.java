package com.example.caloryxbackend.foodlog;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.foodlog.payload.request.FoodLogAmountUpdateRequest;
import com.example.caloryxbackend.foodlog.payload.request.FoodLogRequest;
import com.example.caloryxbackend.foodlog.payload.response.FoodLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodLogService {

    private final CurrentUserService currentUserService;
    private final FoodLogRepository repository;
    private final FoodLogMapper foodLogMapper;

    @Transactional
    public FoodLogResponse createFoodLog(FoodLogRequest request) {
        User user = currentUserService.getUser();
        FoodLog entity = foodLogMapper.toEntity(request, user);
        FoodLog saved = repository.save(entity);
        return foodLogMapper.toResponse(saved);
    }

    @Transactional
    public FoodLogResponse updateFoodLogAmount(UUID foodLogId, FoodLogAmountUpdateRequest request) {

        User user = currentUserService.getUser();

        FoodLog foodLog = findFoodLog(foodLogId);

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
        foodLog.setUpdatedBy(user.getId());

        FoodLog saved = repository.save(foodLog);
        return foodLogMapper.toResponse(saved);
    }

    @Transactional
    public void deleteFoodLog(UUID foodLogId) {
        FoodLog foodLog = findFoodLog(foodLogId);

        repository.delete(foodLog);
    }

    private FoodLog findFoodLog(UUID foodLogId){
        User user = currentUserService.getUser();

        return repository.findByIdAndUserId(foodLogId, user.getId())
                .orElseThrow(() -> new NotFoundException("Food log not found"));
    }

    private Double scale(Double value, double ratio) {
        return value == null ? null : value * ratio;
    }
}
