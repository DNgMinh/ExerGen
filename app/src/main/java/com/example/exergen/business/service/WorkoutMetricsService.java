package com.example.exergen.business.service;

import com.example.exergen.model.Workout;

public final class WorkoutMetricsService {
    private WorkoutMetricsService() {
    }

    public static int calculateTotalDurationSeconds(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("workout required");
        }

        int perRound = 0;
        for (int i = 0; i < workout.getExerciseIds().size(); i++) {
            perRound += workout.getWorkSeconds().get(i) + workout.getRestSeconds().get(i);
        }
        return perRound * workout.getRounds();
    }
}
