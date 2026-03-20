package com.example.caloryxbackend.validation;

import com.example.caloryxbackend.coachprofile.payload.CoachProfileRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, CoachProfileRequest> {

    @Override
    public boolean isValid(CoachProfileRequest req, ConstraintValidatorContext context) {

        if (req == null) {
            return true;
        }

        if (req.getPriceFrom() != null && req.getPriceTo() != null) {
            if (req.getPriceFrom() > req.getPriceTo()) {
                return false;
            }
        }

        if ((req.getPriceFrom() != null || req.getPriceTo() != null) && req.getCurrency() == null) {
            return false;
        }

        return true;
    }
}