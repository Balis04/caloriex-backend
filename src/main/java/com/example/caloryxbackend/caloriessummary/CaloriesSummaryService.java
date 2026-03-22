package com.example.caloryxbackend.caloriessummary;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.caloriessummary.calculation.DailyMacroTargets;
import com.example.caloryxbackend.caloriessummary.repository.CaloriesSummaryRepository;
import com.example.caloryxbackend.caloriessummary.repository.DayIntakeProjection;
import com.example.caloryxbackend.caloriessummary.calculation.MealCaloriesBreakdown;
import com.example.caloryxbackend.caloriessummary.calculation.MealMacroTotals;
import com.example.caloryxbackend.caloriessummary.payload.CaloriesSummaryResponse;
import com.example.caloryxbackend.caloriessummary.payload.FoodItemResponse;
import com.example.caloryxbackend.caloriessummary.payload.MealTimeGroupResponse;
import com.example.caloryxbackend.common.enums.MealTime;
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

    private final CurrentUserService currentUserService;
    private final CaloriesSummaryRepository caloriesSummaryRepository;
    private final CaloriesCalculator caloriesCalculator;
    private final CaloriesSummaryMapper caloriesSummaryMapper;

    public CaloriesSummaryResponse getSummaryByDate(LocalDate date) {
        User user = currentUserService.getUser();

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        DayIntakeProjection intake =
                caloriesSummaryRepository.findTodayIntake(user.getAuth0id(), start, end);

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
        User user = currentUserService.getUser();

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<FoodLog> logs = caloriesSummaryRepository
                .findByAuth0IdAndMealTimeAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                        user.getAuth0id(),
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
        User user = currentUserService.getUser();

        List<FoodLog> logs = getLogsForDate(user, LocalDate.now());

        return caloriesSummaryMapper.toFoodItemList(logs);
    }

    private List<FoodLog> getLogsForDate(User user, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return caloriesSummaryRepository
                .findByAuth0IdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
                        user.getAuth0id(), start, end
                );
    }
}
