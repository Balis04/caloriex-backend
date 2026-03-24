package com.example.caloryxbackend.caloriesummary;

import com.example.caloryxbackend.caloriesummary.calculation.DailyMacroTargets;
import com.example.caloryxbackend.caloriesummary.repository.CaloriesSummaryRepository;
import com.example.caloryxbackend.caloriesummary.repository.DayIntakeProjection;
import com.example.caloryxbackend.caloriesummary.calculation.MealCaloriesBreakdown;
import com.example.caloryxbackend.caloriesummary.calculation.MealMacroTotals;
import com.example.caloryxbackend.caloriesummary.payload.CaloriesSummaryResponse;
import com.example.caloryxbackend.caloriesummary.payload.FoodItemResponse;
import com.example.caloryxbackend.caloriesummary.payload.MealTimeGroupResponse;
import com.example.caloryxbackend.common.enums.MealTime;
import com.example.caloryxbackend.common.security.AuthenticatedUserService;
import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaloriesSummaryService {

    private final AuthenticatedUserService authenticatedUserService;
    private final CaloriesSummaryRepository caloriesSummaryRepository;
    private final CaloriesCalculator caloriesCalculator;
    private final CaloriesSummaryMapper caloriesSummaryMapper;

    public CaloriesSummaryResponse getSummaryByDate(LocalDate date) {
        User user = authenticatedUserService.getUser();

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        DayIntakeProjection intake =
                caloriesSummaryRepository.findTodayIntake(user.getId(), start, end);

        List<FoodLog> logs = getLogsForDate(user, targetDate);

        double targetCalories = caloriesCalculator.calculateTargetCalories(user);
        DailyMacroTargets targetMacros = caloriesCalculator.calculateMacros(user, targetCalories);

        MealCaloriesBreakdown mealCaloriesBreakdown = caloriesCalculator.calculateMealCalories(targetCalories, logs);

        return caloriesSummaryMapper.toCaloriesSummaryResponse(
                targetDate,
                targetCalories,
                targetMacros,
                intake,
                mealCaloriesBreakdown);
    }

    public MealTimeGroupResponse getMealTimeSummaryByDateAndMeal(LocalDate date, MealTime mealTime) {
        User user = authenticatedUserService.getUser();

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<FoodLog> logs = caloriesSummaryRepository
                .findByUserIdAndMealTimeAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                        user.getId(),
                        mealTime,
                        start,
                        end
                );

        double dailyTargetCalories = caloriesCalculator.calculateTargetCalories(user);
        DailyMacroTargets dailyTargetMacros = caloriesCalculator.calculateMacros(user, dailyTargetCalories);
        double ratio = mealTime.getRatio();

        MealMacroTotals consumed = caloriesCalculator.calculateMealMacros(logs);

        double targetCaloriesForMeal = dailyTargetCalories * ratio;
        double targetProteinForMeal = dailyTargetMacros.proteinGrams() * ratio;
        double targetCarbsForMeal = dailyTargetMacros.carbohydratesGrams() * ratio;
        double targetFatForMeal = dailyTargetMacros.fatGrams() * ratio;

        return caloriesSummaryMapper.toMealTimeGroupResponse(
                mealTime,
                targetCaloriesForMeal,
                targetProteinForMeal,
                targetCarbsForMeal,
                targetFatForMeal,
                consumed,
                caloriesSummaryMapper.toFoodItemList(logs)
        );
    }

    public List<FoodItemResponse> getTodayFoods() {
        User user = authenticatedUserService.getUser();

        List<FoodLog> logs = getLogsForDate(user, LocalDate.now());

        return caloriesSummaryMapper.toFoodItemList(logs);
    }

    private List<FoodLog> getLogsForDate(User user, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return caloriesSummaryRepository
                .findByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                        user.getId(), start, end
                );
    }
}
