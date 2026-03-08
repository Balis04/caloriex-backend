package com.example.caloryxbackend.foodlog;

import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.foodlog.payload.FoodLogAmountUpdateRequest;
import com.example.caloryxbackend.foodlog.payload.FoodLogRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/food-log")
@RequiredArgsConstructor
public class FoodLogController {

    private final FoodLogService foodLogService;

    @PostMapping("/create")
    public ResponseEntity<?> createFoodLog(@Valid @RequestBody FoodLogRequest request) {
        FoodLog foodLog = foodLogService.createFoodLog(request);
        return ResponseEntity.ok(foodLog);
    }

    @PatchMapping("/{id}/amount")
    public ResponseEntity<FoodLog> updateFoodLogAmount(
            @PathVariable("id") UUID id,
            @Valid @RequestBody FoodLogAmountUpdateRequest request
    ) {
        return ResponseEntity.ok(foodLogService.updateFoodLogAmount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodLog(@PathVariable("id") UUID id) {
        foodLogService.deleteFoodLog(id);
        return ResponseEntity.noContent().build();
    }
}
