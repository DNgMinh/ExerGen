package com.example.exergen.persistence;

import static org.junit.Assert.assertEquals;

import com.example.exergen.model.Workout;

import org.junit.Test;

import java.util.List;

public class DefaultWorkoutSeedDataTest {

    @Test
    public void createDefaultWorkouts_returnsExpectedCanonicalSeedData() {
        List<Workout> defaults = DefaultWorkoutSeedData.createDefaultWorkouts();

        assertEquals(2, defaults.size());

        Workout w1 = defaults.get(0);
        assertEquals("w1", w1.getId());
        assertEquals("Beginner Full Body", w1.getName());
        assertEquals(3, w1.getRounds());
        assertEquals(List.of("ex_1", "ex_2"), w1.getExerciseIds());
        assertEquals(List.of(30, 45), w1.getWorkSeconds());
        assertEquals(List.of(15, 15), w1.getRestSeconds());

        Workout w2 = defaults.get(1);
        assertEquals("w2", w2.getId());
        assertEquals("Upper Body Blast", w2.getName());
        assertEquals(4, w2.getRounds());
        assertEquals(List.of("ex_1"), w2.getExerciseIds());
        assertEquals(List.of(45), w2.getWorkSeconds());
        assertEquals(List.of(20), w2.getRestSeconds());
    }
}
