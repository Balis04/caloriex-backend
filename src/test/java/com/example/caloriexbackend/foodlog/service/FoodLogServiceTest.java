package com.example.caloriexbackend.foodlog.service;

import com.example.caloriexbackend.common.enums.MealTime;
import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.FoodLog;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.foodlog.mapper.FoodLogMapper;
import com.example.caloriexbackend.foodlog.payload.request.FoodLogAmountUpdateRequest;
import com.example.caloriexbackend.foodlog.payload.request.FoodLogRequest;
import com.example.caloriexbackend.foodlog.payload.response.FoodLogResponse;
import com.example.caloriexbackend.foodlog.repository.FoodLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodLogServiceTest {

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private FoodLogRepository repository;

    @Mock
    private FoodLogMapper foodLogMapper;

    @InjectMocks
    private FoodLogService foodLogService;

    @Test
    void createFoodLogSuccessfully() {
        User user = user();
        FoodLogRequest request = foodLogRequest();
        FoodLog entity = foodLog(user);
        FoodLogResponse response = response(entity);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(foodLogMapper.toEntity(request, user)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(foodLogMapper.toResponse(entity)).thenReturn(response);

        FoodLogResponse actual = foodLogService.createFoodLog(request);

        assertSame(response, actual);
        verify(foodLogMapper).toEntity(request, user);
        verify(repository).save(entity);
        verify(foodLogMapper).toResponse(entity);
    }

    @Test
    void updateFoodLogAmountSuccessfully() {
        User user = user();
        UUID foodLogId = UUID.randomUUID();
        FoodLogAmountUpdateRequest request = amountRequest(300.0);
        FoodLog foodLog = foodLog(user);
        foodLog.setId(foodLogId);
        foodLog.setAmount(150.0);
        foodLog.setCalories(400.0);
        foodLog.setProtein(30.0);
        foodLog.setCarbohydrates(50.0);
        foodLog.setFat(10.0);
        FoodLogResponse response = response(foodLog);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(repository.findByIdAndUserId(foodLogId, user.getId())).thenReturn(Optional.of(foodLog));
        when(repository.save(foodLog)).thenReturn(foodLog);
        when(foodLogMapper.toResponse(foodLog)).thenReturn(response);

        FoodLogResponse actual = foodLogService.updateFoodLogAmount(foodLogId, request);

        assertSame(response, actual);
        assertEquals(300.0, foodLog.getAmount());
        assertEquals(800.0, foodLog.getCalories());
        assertEquals(60.0, foodLog.getProtein());
        assertEquals(100.0, foodLog.getCarbohydrates());
        assertEquals(20.0, foodLog.getFat());
        verify(repository).save(foodLog);
    }

    @Test
    void updateFoodLogAmountKeepNullValuesAsNull() {
        User user = user();
        UUID foodLogId = UUID.randomUUID();
        FoodLogAmountUpdateRequest request = amountRequest(200.0);
        FoodLog foodLog = foodLog(user);
        foodLog.setId(foodLogId);
        foodLog.setAmount(100.0);
        foodLog.setCalories(250.0);
        foodLog.setProtein(null);
        foodLog.setCarbohydrates(20.0);
        foodLog.setFat(null);
        FoodLogResponse response = response(foodLog);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(repository.findByIdAndUserId(foodLogId, user.getId())).thenReturn(Optional.of(foodLog));
        when(repository.save(foodLog)).thenReturn(foodLog);
        when(foodLogMapper.toResponse(foodLog)).thenReturn(response);

        foodLogService.updateFoodLogAmount(foodLogId, request);

        assertEquals(500.0, foodLog.getCalories());
        assertNull(foodLog.getProtein());
        assertEquals(40.0, foodLog.getCarbohydrates());
        assertNull(foodLog.getFat());
    }

    @Test
    void updateFoodLogAmountAmountIsMissing() {
        User user = user();
        UUID foodLogId = UUID.randomUUID();
        FoodLogAmountUpdateRequest request = amountRequest(200.0);
        FoodLog foodLog = foodLog(user);
        foodLog.setId(foodLogId);
        foodLog.setAmount(null);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(repository.findByIdAndUserId(foodLogId, user.getId())).thenReturn(Optional.of(foodLog));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> foodLogService.updateFoodLogAmount(foodLogId, request)
        );

        assertEquals("Cannot recalculate macros and calories because original amount is missing or zero", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void updateFoodLogIsNotFound() {
        User user = user();
        UUID foodLogId = UUID.randomUUID();
        FoodLogAmountUpdateRequest request = amountRequest(200.0);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(repository.findByIdAndUserId(foodLogId, user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> foodLogService.updateFoodLogAmount(foodLogId, request)
        );

        assertEquals("Food log not found", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deleteFoodLogSuccessfully() {
        User user = user();
        UUID foodLogId = UUID.randomUUID();
        FoodLog foodLog = foodLog(user);
        foodLog.setId(foodLogId);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(repository.findByIdAndUserId(foodLogId, user.getId())).thenReturn(Optional.of(foodLog));

        foodLogService.deleteFoodLog(foodLogId);

        verify(repository).delete(foodLog);
    }

    @Test
    void deleteFoodLogIsMissing() {
        User user = user();
        UUID foodLogId = UUID.randomUUID();

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(repository.findByIdAndUserId(foodLogId, user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> foodLogService.deleteFoodLog(foodLogId)
        );

        assertEquals("Food log not found", exception.getMessage());
        verify(repository, never()).delete(any());
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private FoodLogRequest foodLogRequest() {
        FoodLogRequest request = new FoodLogRequest();
        request.setFoodName("Rice");
        request.setMealTime(MealTime.LUNCH);
        request.setAmount(150.0);
        request.setUnit("g");
        request.setCalories(180.0);
        request.setProtein(4.0);
        request.setCarbohydrates(40.0);
        request.setFat(1.0);
        request.setConsumedAt(LocalDateTime.of(2026, 4, 8, 12, 0));
        return request;
    }

    private FoodLogAmountUpdateRequest amountRequest(Double amount) {
        FoodLogAmountUpdateRequest request = new FoodLogAmountUpdateRequest();
        request.setAmount(amount);
        return request;
    }

    private FoodLog foodLog(User user) {
        FoodLog foodLog = new FoodLog();
        foodLog.setId(UUID.randomUUID());
        foodLog.setUser(user);
        foodLog.setMealTime(MealTime.LUNCH);
        foodLog.setFoodName("Rice");
        foodLog.setAmount(150.0);
        foodLog.setUnit("g");
        foodLog.setCalories(180.0);
        foodLog.setProtein(4.0);
        foodLog.setCarbohydrates(40.0);
        foodLog.setFat(1.0);
        foodLog.setConsumedAt(LocalDateTime.of(2026, 4, 8, 12, 0));
        return foodLog;
    }

    private FoodLogResponse response(FoodLog foodLog) {
        return new FoodLogResponse(
                foodLog.getId(),
                foodLog.getUser().getId(),
                foodLog.getMealTime(),
                foodLog.getFoodName(),
                foodLog.getAmount(),
                foodLog.getUnit(),
                foodLog.getCalories(),
                foodLog.getProtein(),
                foodLog.getCarbohydrates(),
                foodLog.getFat(),
                foodLog.getConsumedAt(),
                LocalDateTime.of(2026, 4, 8, 11, 0),
                LocalDateTime.of(2026, 4, 8, 13, 0)
        );
    }
}
