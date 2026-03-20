package com.example.caloryxbackend.caloriesummary;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.caloriesummary.payload.CaloriesSummaryResponse;
import com.example.caloryxbackend.caloriesummary.payload.MealTimeGroupResponse;
import com.example.caloryxbackend.caloriesummary.payload.TodayFoodItemResponse;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.common.enums.MealTime;
import com.example.caloryxbackend.user.UserRepository;
import com.example.caloryxbackend.user.model.enums.ActivityLevel;
import com.example.caloryxbackend.user.model.enums.Gender;
import com.example.caloryxbackend.user.model.enums.GoalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaloriesSummaryService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final CaloriesSummaryRepository caloriesSummaryRepository;

    public CaloriesSummaryResponse getSummaryByDate(LocalDate date) {
        String auth0Id = currentUserService.getAuth0Id();
        User user = userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        CaloriesSummaryRepository.TodayIntakeProjection intake =
                caloriesSummaryRepository.findTodayIntake(auth0Id, start, end);

        List<FoodLog> logs = caloriesSummaryRepository
                .findByAuth0IdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(auth0Id, start, end);

        double targetCalories = calculateTargetCalories(user);
        MacroTargets targetMacros = calculateTargetMacros(targetCalories, user.getGoal());

        double targetBreakfastKcal = targetCalories * getMealRatio(MealTime.BREAKFAST);
        double targetLunchKcal = targetCalories * getMealRatio(MealTime.LUNCH);
        double targetDinnerKcal = targetCalories * getMealRatio(MealTime.DINNER);
        double targetSnackKcal = targetCalories * getMealRatio(MealTime.SNACK);

        double consumedBreakfastKcal = sumConsumedCaloriesByMeal(logs, MealTime.BREAKFAST);
        double consumedLunchKcal = sumConsumedCaloriesByMeal(logs, MealTime.LUNCH);
        double consumedDinnerKcal = sumConsumedCaloriesByMeal(logs, MealTime.DINNER);
        double consumedSnackKcal = sumConsumedCaloriesByMeal(logs, MealTime.SNACK);

        return new CaloriesSummaryResponse(
                targetDate,
                targetCalories,
                targetMacros.proteinGrams(),
                targetMacros.carbohydratesGrams(),
                targetMacros.fatGrams(),
                nullSafe(intake.getCalories()),
                nullSafe(intake.getProtein()),
                nullSafe(intake.getCarbohydrates()),
                nullSafe(intake.getFat()),
                targetBreakfastKcal,
                consumedBreakfastKcal,
                targetLunchKcal,
                consumedLunchKcal,
                targetDinnerKcal,
                consumedDinnerKcal,
                targetSnackKcal,
                consumedSnackKcal
        );
    }

    public List<TodayFoodItemResponse> getTodayFoods() {
        String auth0Id = currentUserService.getAuth0Id();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<FoodLog> logs = caloriesSummaryRepository
                .findByAuth0IdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(auth0Id, start, end);

        return logs.stream()
                .map(this::mapToTodayFoodItem)
                .toList();
    }

    public MealTimeGroupResponse getMealTimeSummaryByDateAndMeal(LocalDate date, MealTime mealTime) {
        String auth0Id = currentUserService.getAuth0Id();
        User user = userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<FoodLog> logs = caloriesSummaryRepository
                .findByAuth0IdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(auth0Id, start, end)
                .stream()
                .filter(log -> mealTime.equals(log.getMealTime()))
                .toList();

        double dailyTargetCalories = calculateTargetCalories(user);
        MacroTargets dailyTargetMacros = calculateTargetMacros(dailyTargetCalories, user.getGoal());
        double ratio = getMealRatio(mealTime);

        double consumedCalories = logs.stream().map(FoodLog::getCalories).filter(v -> v != null).mapToDouble(Double::doubleValue).sum();
        double consumedProtein = logs.stream().map(FoodLog::getProtein).filter(v -> v != null).mapToDouble(Double::doubleValue).sum();
        double consumedCarbs = logs.stream().map(FoodLog::getCarbohydrates).filter(v -> v != null).mapToDouble(Double::doubleValue).sum();
        double consumedFat = logs.stream().map(FoodLog::getFat).filter(v -> v != null).mapToDouble(Double::doubleValue).sum();

        return new MealTimeGroupResponse(
                mealTime,
                dailyTargetCalories * ratio,
                dailyTargetMacros.proteinGrams() * ratio,
                dailyTargetMacros.carbohydratesGrams() * ratio,
                dailyTargetMacros.fatGrams() * ratio,
                consumedCalories,
                consumedProtein,
                consumedCarbs,
                consumedFat,
                logs.stream().map(this::mapToTodayFoodItem).toList()
        );
    }

    private double sumConsumedCaloriesByMeal(List<FoodLog> logs, MealTime mealTime) {
        return logs.stream()
                .filter(log -> mealTime.equals(log.getMealTime()))
                .map(FoodLog::getCalories)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double getMealRatio(MealTime mealTime) {
        return switch (mealTime) {
            case BREAKFAST -> 0.25;
            case LUNCH -> 0.35;
            case DINNER -> 0.30;
            case SNACK -> 0.10;
        };
    }

    private TodayFoodItemResponse mapToTodayFoodItem(FoodLog foodLog) {
        return new TodayFoodItemResponse(
                foodLog.getId(),
                foodLog.getFoodName(),
                foodLog.getMealTime(),
                foodLog.getAmount(),
                foodLog.getUnit(),
                foodLog.getCalories(),
                foodLog.getProtein(),
                foodLog.getCarbohydrates(),
                foodLog.getFat(),
                foodLog.getConsumedAt()
        );
    }

    private double calculateTargetCalories(User user) {
        double bmr = calculateBmr(user);
        double tdee = bmr * activityMultiplier(user.getActivityLevel());
        double adjusted = tdee + goalAdjustment(user.getGoal(), user.getWeeklyGoalKg());

        return Math.max(0, adjusted);
    }

    private double calculateBmr(User user) {
        if (user.getBirthDate() == null || user.getHeightCm() == null) {
            throw new BadRequestException(
                    "User profile incomplete. Birth date and height must be set before calculating BMR."
            );
        }

        double weight = resolveWeight(user);
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        double base = (10 * weight) + (6.25 * user.getHeightCm()) - (5 * age);

        Gender gender = user.getGender() != null ? user.getGender() : Gender.OTHER;
        return switch (gender) {
            case MALE -> base + 5;
            case FEMALE -> base - 161;
            case OTHER -> base - 78;
        };
    }

    private double resolveWeight(User user) {
        if (user.getActualWeightKg() != null) {
            return user.getActualWeightKg();
        }
        if (user.getStartWeightKg() != null) {
            return user.getStartWeightKg();
        }
        throw new BadRequestException("Missing required profile data: actualWeightKg or startWeightKg");
    }

    private double activityMultiplier(ActivityLevel activityLevel) {
        ActivityLevel level = activityLevel != null ? activityLevel : ActivityLevel.MODERATE;
        return switch (level) {
            case SEDENTARY -> 1.2;
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
        };
    }

    private double goalAdjustment(GoalType goal, Double weeklyGoalKg) {
        GoalType userGoal = goal != null ? goal : GoalType.MAINTAIN;
        double weekly = weeklyGoalKg != null ? Math.abs(weeklyGoalKg) : 0;
        double dailyDelta = (weekly * 7700) / 7.0;

        return switch (userGoal) {
            case CUT -> -dailyDelta;
            case BULK -> dailyDelta;
            case MAINTAIN -> 0;
        };
    }

    private MacroTargets calculateTargetMacros(double targetCalories, GoalType goal) {
        GoalType userGoal = goal != null ? goal : GoalType.MAINTAIN;

        double proteinRatio;
        double carbsRatio;
        double fatRatio;

        switch (userGoal) {
            case CUT -> {
                proteinRatio = 0.35;
                carbsRatio = 0.40;
                fatRatio = 0.25;
            }
            case BULK -> {
                proteinRatio = 0.25;
                carbsRatio = 0.50;
                fatRatio = 0.25;
            }
            default -> {
                proteinRatio = 0.30;
                carbsRatio = 0.40;
                fatRatio = 0.30;
            }
        }

        double proteinGrams = (targetCalories * proteinRatio) / 4.0;
        double carbohydratesGrams = (targetCalories * carbsRatio) / 4.0;
        double fatGrams = (targetCalories * fatRatio) / 9.0;

        return new MacroTargets(proteinGrams, carbohydratesGrams, fatGrams);
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0;
    }

    private record MacroTargets(double proteinGrams, double carbohydratesGrams, double fatGrams) {
    }
}
