package com.example.caloryxbackend.coachprofile.coachavailability.payload;

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

    private LocalTime startTime;

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
