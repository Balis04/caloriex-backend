package com.example.caloryxbackend.food_log;

import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.food_log.payload.FoodLodRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/food-log")
@RequiredArgsConstructor
public class FoodLogController {

    private final FoodLogService foodLogService;

    @PostMapping("/create")
    public ResponseEntity<?> createFoodLog( @Valid @RequestBody FoodLodRequest request){
        FoodLog foodLog = foodLogService.createFoodLog(request);
        return ResponseEntity.ok(foodLog);
    }
}
