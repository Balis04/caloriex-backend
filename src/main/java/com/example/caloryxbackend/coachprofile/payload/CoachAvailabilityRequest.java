package com.example.caloryxbackend.coachprofile.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class CoachAvailabilityRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Availability flag is required")
    private Boolean available;

    private LocalTime startTime;

    private LocalTime endTime;
}
