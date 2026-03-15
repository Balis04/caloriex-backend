package com.example.caloryxbackend.trainingrequest.payload;

import com.example.caloryxbackend.trainingrequest.model.TrainingRequestStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrainingRequestCreateRequest(
        @NotNull
        @Min(1)
        @Max(14)
        Integer weeklyTrainingCount,

        @NotNull
        @Min(15)
        @Max(480)
        Integer sessionDurationMinutes,

        @NotBlank
        @Size(max = 255)
        String preferredLocation,

        TrainingRequestStatus status,

        @Size(max = 5000)
        String coachNote
) {
}
