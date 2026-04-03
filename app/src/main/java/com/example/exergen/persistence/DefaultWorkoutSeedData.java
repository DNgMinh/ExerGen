package com.example.exergen.persistence;

import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.List;

public final class DefaultWorkoutSeedData {
    private DefaultWorkoutSeedData() {
    }

    public static List<Workout> createDefaultWorkouts() {
        return List.of(
                new Workout(
                        "w1",
                        "Beginner Full Body",
                        3,
                        List.of(
                                new WorkoutStep("ex_1", 30, 15),
                                new WorkoutStep("ex_2", 45, 15))),
                new Workout(
                        "w2",
                        "Upper Body Blast",
                        4,
                        List.of(new WorkoutStep("ex_1", 45, 20))));
    }
}
