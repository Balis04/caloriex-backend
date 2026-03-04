package com.example.caloryxbackend.foodlog.payload;

import com.example.caloryxbackend.foodlog.MealTime;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodLogRequest {
    @NotBlank(message = "Az étel neve nem lehet üres")
    @Size(max = 100, message = "Az étel neve túl hosszú (max 100 karakter)")
    private String foodName;

    @NotNull(message = "Az étkezés időpontja (reggeli, ebéd, stb.) kötelező")
    private MealTime mealTime;

    @NotNull(message = "A mennyiség megadása kötelező")
    @Positive(message = "A mennyiségnek pozitívnak kell lennie")
    private Double amount;

    @NotBlank(message = "A mértékegység (pl. g, ml) kötelező")
    private String unit;

    @NotNull(message = "A kalória megadása kötelező")
    @Min(value = 0, message = "A kalória nem lehet negatív")
    private Double calories;

    @DecimalMin("0.0")
    private Double protein;

    @DecimalMin("0.0")
    private Double carbohydrates;

    @DecimalMin("0.0")
    private Double fat;

    // Opcionális: Ha a frontend küldi, akkor azt használjuk, ha nem, a backend generálja
    private LocalDateTime consumedAt;
}
