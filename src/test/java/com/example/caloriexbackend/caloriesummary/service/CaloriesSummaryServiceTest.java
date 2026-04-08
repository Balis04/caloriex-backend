package com.example.caloriexbackend.caloriesummary.service;

import com.example.caloriexbackend.caloriesummary.calculation.CaloriesCalculator;
import com.example.caloriexbackend.caloriesummary.calculation.DailyMacroTargets;
import com.example.caloriexbackend.caloriesummary.calculation.MealCaloriesBreakdown;
import com.example.caloriexbackend.caloriesummary.calculation.MealMacroTotals;
import com.example.caloriexbackend.caloriesummary.mapper.CaloriesSummaryMapper;
import com.example.caloriexbackend.caloriesummary.payload.CaloriesSummaryResponse;
import com.example.caloriexbackend.caloriesummary.payload.FoodItemResponse;
import com.example.caloriexbackend.caloriesummary.payload.MealTimeGroupResponse;
import com.example.caloriexbackend.caloriesummary.repository.CaloriesSummaryRepository;
import com.example.caloriexbackend.caloriesummary.repository.DayIntakeProjection;
import com.example.caloriexbackend.common.enums.MealTime;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.FoodLog;
import com.example.caloriexbackend.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaloriesSummaryServiceTest {

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private CaloriesSummaryRepository caloriesSummaryRepository;

    @Mock
    private CaloriesCalculator caloriesCalculator;

    @Mock
    private CaloriesSummaryMapper caloriesSummaryMapper;

    @InjectMocks
    private CaloriesSummaryService caloriesSummaryService;

    @Test
    void getSummaryByDateShouldUseProvidedDateAndReturnMappedSummary() {
        User user = user();
        LocalDate date = LocalDate.of(2026, 4, 8);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        DayIntakeProjection intake = dayIntake(1850.0, 140.0, 190.0, 60.0);
        List<FoodLog> logs = List.of(foodLog(MealTime.BREAKFAST), foodLog(MealTime.LUNCH));
        DailyMacroTargets macros = new DailyMacroTargets(150.0, 200.0, 70.0);
        MealCaloriesBreakdown mealBreakdown = new MealCaloriesBreakdown(500.0, 700.0, 600.0, 200.0, 450.0, 800.0, 500.0, 100.0);
        CaloriesSummaryResponse response = new CaloriesSummaryResponse(
                date, 2200.0, 150.0, 200.0, 70.0,
                1850.0, 140.0, 190.0, 60.0,
                500.0, 450.0, 700.0, 800.0, 600.0, 500.0, 200.0, 100.0
        );

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(caloriesSummaryRepository.findTodayIntake(user.getId(), start, end)).thenReturn(intake);
        when(caloriesSummaryRepository.findByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(user.getId(), start, end))
                .thenReturn(logs);
        when(caloriesCalculator.calculateTargetCalories(user)).thenReturn(2200.0);
        when(caloriesCalculator.calculateMacros(user, 2200.0)).thenReturn(macros);
        when(caloriesCalculator.calculateMealCalories(2200.0, logs)).thenReturn(mealBreakdown);
        when(caloriesSummaryMapper.toCaloriesSummaryResponse(date, 2200.0, macros, intake, mealBreakdown)).thenReturn(response);

        CaloriesSummaryResponse actual = caloriesSummaryService.getSummaryByDate(date);

        assertSame(response, actual);
        verify(caloriesSummaryRepository).findTodayIntake(user.getId(), start, end);
        verify(caloriesSummaryRepository).findByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(user.getId(), start, end);
        verify(caloriesCalculator).calculateMealCalories(2200.0, logs);
    }

    @Test
    void getMealTimeSummaryByDateAndMealShouldCalculateMealTargetsAndMapResponse() {
        User user = user();
        LocalDate date = LocalDate.of(2026, 4, 8);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<FoodLog> logs = List.of(foodLog(MealTime.DINNER));
        DailyMacroTargets dailyMacros = new DailyMacroTargets(160.0, 240.0, 80.0);
        MealMacroTotals consumed = new MealMacroTotals(520.0, 42.0, 35.0, 18.0);
        List<FoodItemResponse> foods = List.of(new FoodItemResponse(
                UUID.randomUUID(),
                "Chicken",
                MealTime.DINNER,
                200.0,
                "g",
                330.0,
                40.0,
                0.0,
                8.0,
                start.plusHours(18)
        ));
        MealTimeGroupResponse response = new MealTimeGroupResponse(
                MealTime.DINNER,
                660.0,
                48.0,
                72.0,
                24.0,
                520.0,
                42.0,
                35.0,
                18.0,
                foods
        );

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(caloriesSummaryRepository.findByUserIdAndMealTimeAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                user.getId(), MealTime.DINNER, start, end
        )).thenReturn(logs);
        when(caloriesCalculator.calculateTargetCalories(user)).thenReturn(2200.0);
        when(caloriesCalculator.calculateMacros(user, 2200.0)).thenReturn(dailyMacros);
        when(caloriesCalculator.calculateMealMacros(logs)).thenReturn(consumed);
        when(caloriesSummaryMapper.toFoodItemList(logs)).thenReturn(foods);
        when(caloriesSummaryMapper.toMealTimeGroupResponse(
                MealTime.DINNER,
                660.0,
                48.0,
                72.0,
                24.0,
                consumed,
                foods
        )).thenReturn(response);

        MealTimeGroupResponse actual = caloriesSummaryService.getMealTimeSummaryByDateAndMeal(date, MealTime.DINNER);

        assertSame(response, actual);
        verify(caloriesSummaryRepository).findByUserIdAndMealTimeAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                user.getId(), MealTime.DINNER, start, end
        );
        verify(caloriesCalculator).calculateMealMacros(logs);
    }

    @Test
    void getTodayFoodsShouldReturnMappedFoodsForCurrentDate() {
        User user = user();
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<FoodLog> logs = List.of(foodLog(MealTime.SNACK));
        List<FoodItemResponse> foods = List.of(new FoodItemResponse(
                UUID.randomUUID(),
                "Banana",
                MealTime.SNACK,
                1.0,
                "piece",
                100.0,
                1.0,
                23.0,
                0.0,
                start.plusHours(15)
        ));

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(caloriesSummaryRepository.findByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(user.getId(), start, end))
                .thenReturn(logs);
        when(caloriesSummaryMapper.toFoodItemList(logs)).thenReturn(foods);

        List<FoodItemResponse> actual = caloriesSummaryService.getTodayFoods();

        assertEquals(foods, actual);
    }

    @Test
    void getSummaryByDateShouldUseCurrentDateWhenInputIsNull() {
        User user = user();
        DailyMacroTargets macros = new DailyMacroTargets(150.0, 200.0, 70.0);
        MealCaloriesBreakdown mealBreakdown = new MealCaloriesBreakdown(500.0, 700.0, 600.0, 200.0, 0.0, 0.0, 0.0, 0.0);
        DayIntakeProjection intake = dayIntake(0.0, 0.0, 0.0, 0.0);
        CaloriesSummaryResponse response = new CaloriesSummaryResponse(
                LocalDate.now(), 2200.0, 150.0, 200.0, 70.0,
                0.0, 0.0, 0.0, 0.0,
                500.0, 0.0, 700.0, 0.0, 600.0, 0.0, 200.0, 0.0
        );

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(caloriesCalculator.calculateTargetCalories(user)).thenReturn(2200.0);
        when(caloriesCalculator.calculateMacros(user, 2200.0)).thenReturn(macros);
        when(caloriesSummaryRepository.findTodayIntake(
                org.mockito.ArgumentMatchers.eq(user.getId()),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)
        )).thenReturn(intake);
        when(caloriesSummaryRepository.findByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                org.mockito.ArgumentMatchers.eq(user.getId()),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)
        )).thenReturn(List.of());
        when(caloriesCalculator.calculateMealCalories(2200.0, List.of())).thenReturn(mealBreakdown);
        when(caloriesSummaryMapper.toCaloriesSummaryResponse(
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.eq(2200.0),
                org.mockito.ArgumentMatchers.eq(macros),
                org.mockito.ArgumentMatchers.eq(intake),
                org.mockito.ArgumentMatchers.eq(mealBreakdown)
        )).thenReturn(response);

        CaloriesSummaryResponse actual = caloriesSummaryService.getSummaryByDate(null);

        assertSame(response, actual);

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(caloriesSummaryRepository).findTodayIntake(
                org.mockito.ArgumentMatchers.eq(user.getId()),
                startCaptor.capture(),
                endCaptor.capture()
        );
        assertEquals(LocalDate.now().atStartOfDay(), startCaptor.getValue());
        assertEquals(LocalDate.now().plusDays(1).atStartOfDay(), endCaptor.getValue());
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private FoodLog foodLog(MealTime mealTime) {
        FoodLog foodLog = new FoodLog();
        foodLog.setId(UUID.randomUUID());
        foodLog.setMealTime(mealTime);
        foodLog.setFoodName("Food");
        foodLog.setConsumedAt(LocalDateTime.of(2026, 4, 8, 12, 0));
        return foodLog;
    }

    private DayIntakeProjection dayIntake(double calories, double protein, double carbohydrates, double fat) {
        return new DayIntakeProjection() {
            @Override
            public Double getCalories() {
                return calories;
            }

            @Override
            public Double getProtein() {
                return protein;
            }

            @Override
            public Double getCarbohydrates() {
                return carbohydrates;
            }

            @Override
            public Double getFat() {
                return fat;
            }
        };
    }
}
