package com.example.caloryxbackend.caloriesummary;

import com.example.caloryxbackend.caloriesummary.payload.CaloriesSummaryResponse;
import com.example.caloryxbackend.caloriesummary.payload.MealTimeGroupResponse;
import com.example.caloryxbackend.caloriesummary.payload.TodayFoodItemResponse;
import com.example.caloryxbackend.foodlog.MealTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calories-summary")
@RequiredArgsConstructor
public class CaloriesSummaryController {

    private final CaloriesSummaryService caloriesSummaryService;

    @GetMapping("/today")
    public ResponseEntity<CaloriesSummaryResponse> getSummaryByDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(caloriesSummaryService.getSummaryByDate(date));
    }

    @GetMapping("/today-foods")
    public ResponseEntity<List<TodayFoodItemResponse>> getTodayFoods() {
        return ResponseEntity.ok(caloriesSummaryService.getTodayFoods());
    }

    @GetMapping("/meal-times")
    public ResponseEntity<MealTimeGroupResponse> getMealTimesByDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("mealTime") MealTime mealTime
    ) {
        return ResponseEntity.ok(caloriesSummaryService.getMealTimeSummaryByDateAndMeal(date, mealTime));
    }
}
