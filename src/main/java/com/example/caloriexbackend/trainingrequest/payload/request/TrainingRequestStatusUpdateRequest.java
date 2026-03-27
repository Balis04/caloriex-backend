package com.example.caloriexbackend.trainingrequest.payload.request;

import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrainingRequestStatusUpdateRequest(
        @NotNull
        TrainingRequestStatus status,

        @NotBlank
        @Size(max = 5000)
        String coachResponse
) {
}
