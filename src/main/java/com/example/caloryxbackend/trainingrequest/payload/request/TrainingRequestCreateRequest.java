package com.example.caloryxbackend.trainingrequest.payload.request;

import jakarta.validation.constraints.*;

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

        @Size(max = 5000)
        String coachNote
) {
}
