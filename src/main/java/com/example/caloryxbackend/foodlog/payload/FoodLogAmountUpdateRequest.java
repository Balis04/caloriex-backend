package com.example.caloryxbackend.foodlog.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FoodLogAmountUpdateRequest {

    @NotNull(message = "A mennyiség megadása kötelezõ")
    @Positive(message = "A mennyiségnek pozitívnak kell lennie")
    private Double amount;
}
