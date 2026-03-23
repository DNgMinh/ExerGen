package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;

import com.example.exergen.model.Workout;

import org.junit.Test;

import java.util.List;

public class WorkoutMetricsServiceTest {

    @Test
    public void calculateTotalDurationSecondsComputesAcrossSets() {
        Workout workout = new Workout(
                "w1",
                "Test",
                2,
                List.of("e1", "e2"),
                List.of(30, 45),
                List.of(10, 15));

        int totalSeconds = WorkoutMetricsService.calculateTotalDurationSeconds(workout);

        assertEquals(200, totalSeconds);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateTotalDurationSecondsRejectsNullWorkout() {
        WorkoutMetricsService.calculateTotalDurationSeconds(null);
    }
}
