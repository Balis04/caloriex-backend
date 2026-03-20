package com.example.caloryxbackend.trainingrequest.payload.request;

import com.example.caloryxbackend.common.enums.TrainingRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrainingRequestStatusUpdateRequest(
        @NotNull
        TrainingRequestStatus status,

        @NotBlank
        @Size(max = 5000)
        String description
) {
}
