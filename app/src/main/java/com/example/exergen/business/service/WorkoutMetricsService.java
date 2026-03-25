package com.example.exergen.business.service;

import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

public final class WorkoutMetricsService {
    private WorkoutMetricsService() {
    }

    public static int calculateTotalDurationSeconds(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("workout required");
        }

        int perSet = 0;
        for (WorkoutStep step : workout.getSteps()) {
            perSet += step.getWorkSeconds() + step.getRestSeconds();
        }
        return perSet * workout.getSets();
    }
}
