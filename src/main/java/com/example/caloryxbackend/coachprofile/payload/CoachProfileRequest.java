package com.example.caloryxbackend.coachprofile.payload;

import com.example.caloryxbackend.coachprofile.model.Currency;
import com.example.caloryxbackend.coachprofile.model.TrainingFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CoachProfileRequest {

    private LocalDate trainingStartedAt;

    private String shortDescription;

    private TrainingFormat trainingFormat;

    @Min(value = 0, message = "Price from cannot be negative")
    private Integer priceFrom;

    @Min(value = 0, message = "Price to cannot be negative")
    private Integer priceTo;

    private Currency currency;

    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacity;

    private String contactNote;

    @Valid
    @NotEmpty(message = "At least one availability entry is required")
    private List<CoachAvailabilityRequest> availabilities;

    @Valid
    private List<CoachCertificateRequest> certificates;
}
