package com.example.caloryxbackend.trainingrequest.payload;

import com.example.caloryxbackend.trainingrequest.model.TrainingRequestStatus;
import jakarta.validation.constraints.NotNull;

public record TrainingRequestStatusUpdateRequest(
        @NotNull
        TrainingRequestStatus status
) {
}
