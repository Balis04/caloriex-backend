package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.customfood.payload.CustomFoodRequest;
import com.example.caloryxbackend.customfood.payload.CustomFoodResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/custom-foods")
@RequiredArgsConstructor
public class CustomFoodController {

    private final CustomFoodService customFoodService;

    @PostMapping
    public ResponseEntity<CustomFoodResponse> create(@Valid @RequestBody CustomFoodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customFoodService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomFoodResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomFoodRequest request
    ) {
        return ResponseEntity.ok(customFoodService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customFoodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomFoodResponse>> getAll() {
        return ResponseEntity.ok(customFoodService.getAll());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<CustomFoodResponse>> getMine() {
        return ResponseEntity.ok(customFoodService.getMine());
    }

    @GetMapping("/not-mine")
    public ResponseEntity<List<CustomFoodResponse>> getNotMine() {
        return ResponseEntity.ok(customFoodService.getNotMine());
    }
}
