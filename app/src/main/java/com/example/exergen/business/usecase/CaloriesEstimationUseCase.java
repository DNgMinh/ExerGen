package com.example.exergen.business.usecase;

import com.example.exergen.business.service.CaloriesEstimationService;

public class CaloriesEstimationUseCase {
    private final CaloriesEstimationService service;

    public CaloriesEstimationUseCase(CaloriesEstimationService service) {
        if (service == null) {
            throw new IllegalArgumentException("service required");
        }
        this.service = service;
    }

    public int estimateCalories(int workSeconds, int restSeconds, int intensity) {
        return service.estimateCalories(workSeconds, restSeconds, intensity);
    }

    public int estimateCalories(int durationSeconds, int averageIntensity) {
        return service.estimateCalories(durationSeconds, averageIntensity);
    }

    public int estimateCaloriesWithDefaultIntensity(int workSeconds, int restSeconds) {
        return service.estimateCaloriesWithDefaultIntensity(workSeconds, restSeconds);
    }

    public int estimateCaloriesWithDefaultIntensity(int durationSeconds) {
        return service.estimateCaloriesWithDefaultIntensity(durationSeconds);
    }
}
