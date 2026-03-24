package com.example.caloryxbackend.coachprofile.coachavailability.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class CoachAvailabilityRequest {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private Boolean available;

    @Schema(type = "string", pattern = "HH:mm:ss", example = "10:10:10")
    private LocalTime startTime;

    @Schema(type = "string", pattern = "HH:mm:ss", example = "20:20:20")
    private LocalTime endTime;

    @AssertTrue(message = "Invalid time range")
    public boolean isValidTimeRange() {
        if (!Boolean.TRUE.equals(available)) {
            return true;
        }

        if (startTime == null || endTime == null) {
            return false;
        }

        return startTime.isBefore(endTime);
    }
}
