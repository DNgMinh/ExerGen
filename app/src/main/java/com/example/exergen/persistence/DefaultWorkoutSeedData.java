package com.example.exergen.persistence;

import com.example.exergen.model.Workout;

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
                        List.of("ex_1", "ex_2"),
                        List.of(30, 45),
                        List.of(15, 15)),
                new Workout(
                        "w2",
                        "Upper Body Blast",
                        4,
                        List.of("ex_1"),
                        List.of(45),
                        List.of(20)));
    }
}
