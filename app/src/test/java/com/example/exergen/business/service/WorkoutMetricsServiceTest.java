package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;

import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class WorkoutMetricsServiceTest {

    @Test
    public void testCalculateTotalDuration_ValidWorkout_Success() {
        List<WorkoutStep> steps = Arrays.asList(
                new WorkoutStep("1", 30, 10),
                new WorkoutStep("2", 40, 20)
        );
        Workout workout = new Workout("w1", "Workout", 3, steps);

        // (30+10 + 40+20) * 3 = (40 + 60) * 3 = 100 * 3 = 300
        int duration = WorkoutMetricsService.calculateTotalDurationSeconds(workout);
        assertEquals(300, duration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateTotalDuration_NullWorkout_ThrowsException() {
        WorkoutMetricsService.calculateTotalDurationSeconds(null);
    }
}
