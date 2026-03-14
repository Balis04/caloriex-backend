package com.example.caloryxbackend.coachprofile.payload;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record CoachAvailabilityResponse(
        UUID id,
        DayOfWeek dayOfWeek,
        boolean available,
        LocalTime startTime,
        LocalTime endTime
) {
}
