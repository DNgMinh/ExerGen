package com.example.exergen.business.service;

public class CaloriesEstimationService {
    private static final int MIN_INTENSITY = 1;
    private static final int MAX_INTENSITY = 5;
    private static final int DEFAULT_INTENSITY = 3;
    private static final double BASE_CALORIES_PER_MINUTE = 8.0;
    private static final int SECONDS_PER_MINUTE = 60;

    public int estimateCalories(int durationSeconds, int averageIntensity) {
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("durationSeconds must be >= 0");
        }
        if (averageIntensity < MIN_INTENSITY || averageIntensity > MAX_INTENSITY) {
            throw new IllegalArgumentException("averageIntensity must be between 1 and 5");
        }

        double durationMinutes = (double) durationSeconds / SECONDS_PER_MINUTE;
        double intensityMultiplier = 0.6 + (averageIntensity * 0.2);
        return (int) Math.round(durationMinutes * BASE_CALORIES_PER_MINUTE * intensityMultiplier);
    }

    public int estimateCaloriesWithDefaultIntensity(int durationSeconds) {
        return estimateCalories(durationSeconds, DEFAULT_INTENSITY);
    }
}
