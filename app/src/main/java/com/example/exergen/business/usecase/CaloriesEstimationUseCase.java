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

    public int estimateCalories(int durationSeconds, int averageIntensity) {
        return service.estimateCalories(durationSeconds, averageIntensity);
    }

    public int estimateCaloriesWithDefaultIntensity(int durationSeconds) {
        return service.estimateCaloriesWithDefaultIntensity(durationSeconds);
    }
}
