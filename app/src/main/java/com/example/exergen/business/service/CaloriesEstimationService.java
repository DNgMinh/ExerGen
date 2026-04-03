package com.example.exergen.business.service;

public class CaloriesEstimationService {
    private static final int MIN_INTENSITY = 1;
    private static final int MAX_INTENSITY = 5;
    private static final int DEFAULT_INTENSITY = 3;

    // Simple calorie burn rates per minute
    private static final double WORK_BASE_RATE = 12.0; // kcal/min for work
    private static final double REST_BASE_RATE = 2.0;  // kcal/min for rest
    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * Estimates calories burned by distinguishing between active work time and rest time.
     *
     * @param workSeconds Duration of active exercise
     * @param restSeconds Duration of rest/recovery
     * @param intensity Exercise intensity (1-5)
     * @return Estimated calories burned
     */
    public int estimateCalories(int workSeconds, int restSeconds, int intensity) {
        if (workSeconds < 0 || restSeconds < 0) {
            throw new IllegalArgumentException("Seconds must be >= 0");
        }
        if (intensity < MIN_INTENSITY || intensity > MAX_INTENSITY) {
            throw new IllegalArgumentException("Intensity must be between 1 and 5");
        }

        double workMinutes = (double) workSeconds / SECONDS_PER_MINUTE;
        double restMinutes = (double) restSeconds / SECONDS_PER_MINUTE;

        // Intensity multiplier only applies to work time
        double workMultiplier = 0.6 + (intensity * 0.2); // Level 3 = 1.2x

        double burn = (workMinutes * WORK_BASE_RATE * workMultiplier) +
                     (restMinutes * REST_BASE_RATE);

        return (int) Math.round(burn);
    }

    /**
     * Estimates calories for a single duration (assumes all work time).
     */
    public int estimateCalories(int durationSeconds, int averageIntensity) {
        return estimateCalories(durationSeconds, 0, averageIntensity);
    }

    /**
     * Estimates calories for work/rest using default intensity.
     */
    public int estimateCaloriesWithDefaultIntensity(int workSeconds, int restSeconds) {
        return estimateCalories(workSeconds, restSeconds, DEFAULT_INTENSITY);
    }

    /**
     * Estimates calories for a single duration using default intensity.
     */
    public int estimateCaloriesWithDefaultIntensity(int durationSeconds) {
        return estimateCalories(durationSeconds, 0, DEFAULT_INTENSITY);
    }
}
