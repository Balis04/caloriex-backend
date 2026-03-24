package com.example.caloriexbackend.caloriesummary;

import com.example.caloriexbackend.caloriesummary.payload.CaloriesSummaryResponse;
import com.example.caloriexbackend.caloriesummary.payload.FoodItemResponse;
import com.example.caloriexbackend.caloriesummary.payload.MealTimeGroupResponse;
import com.example.caloriexbackend.common.enums.MealTime;
import com.example.caloriexbackend.common.exception.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Calories Summary", description = "Endpoints for daily calorie and nutrition summaries")
public class CaloriesSummaryController {

    private final CaloriesSummaryService caloriesSummaryService;

    @Operation(
            summary = "Get daily calorie summary",
            description = "Returns calorie targets, consumed macros and meal breakdown for a given date. If no date is provided, today's data is returned."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved daily summary",
                    content = @Content(schema = @Schema(implementation = CaloriesSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping()
    public ResponseEntity<CaloriesSummaryResponse> getSummaryByDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(caloriesSummaryService.getSummaryByDate(date));
    }

    @Operation(
            summary = "Get today's consumed foods",
            description = "Returns all food items consumed today for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved food items",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FoodItemResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    @GetMapping("/today-foods")
    public ResponseEntity<List<FoodItemResponse>> getTodayFoods() {
        return ResponseEntity.ok(caloriesSummaryService.getTodayFoods());
    }

    @Operation(
            summary = "Get meal time summary",
            description = "Returns macro and calorie summary for a specific meal (e.g. BREAKFAST, LUNCH) on a given date."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved meal summary",
                    content = @Content(schema = @Schema(implementation = MealTimeGroupResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid mealTime value",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    @GetMapping("/meal-times")
    public ResponseEntity<MealTimeGroupResponse> getMealTimesByDate(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("mealTime") MealTime mealTime
    ) {
        return ResponseEntity.ok(caloriesSummaryService.getMealTimeSummaryByDateAndMeal(date, mealTime));
    }
}
