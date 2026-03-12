package com.example.caloryxbackend.caloriesummary.payload;

import java.time.LocalDate;
import java.util.List;

public record MealTimeSummaryResponse(
        LocalDate date,
        List<MealTimeGroupResponse> meals
) {
}
