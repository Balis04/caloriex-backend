package com.example.caloriexbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceRangeValidator.class)
@Documented
public @interface ValidPriceRange {

    String message() default "Invalid price range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
