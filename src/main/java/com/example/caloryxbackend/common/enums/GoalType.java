package com.example.caloryxbackend.common.enums;

import lombok.Getter;

@Getter
public enum GoalType {

    CUT(0, "CUT", 0.35, 0.40, 0.25) {
        @Override
        public double applyDailyDelta(double dailyDelta) {
            return -dailyDelta;
        }
    },
    MAINTAIN(1, "MAINTAIN", 0.30, 0.40, 0.30) {
        @Override
        public double applyDailyDelta(double dailyDelta) {
            return 0;
        }
    },
    BULK(2, "BULK", 0.25, 0.50, 0.25) {
        @Override
        public double applyDailyDelta(double dailyDelta) {
            return dailyDelta;
        }
    };

    private final int value;
    private final String name;
    private final double proteinRatio;
    private final double carbsRatio;
    private final double fatRatio;

    GoalType(int value, String name, double proteinRatio, double carbsRatio, double fatRatio) {
        this.value = value;
        this.name = name;
        this.proteinRatio = proteinRatio;
        this.carbsRatio = carbsRatio;
        this.fatRatio = fatRatio;
    }

    public abstract double applyDailyDelta(double dailyDelta);

    public double calculateDailyDelta(Double weeklyGoalKg) {
        double weekly = weeklyGoalKg != null ? Math.abs(weeklyGoalKg) : 0;
        return (weekly * 7700) / 7.0;
    }

    public double calculateAdjustment(Double weeklyGoalKg) {
        return applyDailyDelta(calculateDailyDelta(weeklyGoalKg));
    }
}
