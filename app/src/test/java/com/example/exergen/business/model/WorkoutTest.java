package com.example.exergen.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class WorkoutTest {

    @Test
    public void validWorkout_createsSuccessfully() {
        // Happy Path: Everything is valid
        Workout workout = new Workout(
                "w1",
                "Morning Routine",
                3,
                List.of("e1", "e2"),
                List.of(30, 30),
                List.of(10, 10)
        );

        assertEquals("w1", workout.getId());
        assertEquals("Morning Routine", workout.getName());
        assertEquals(3, workout.getRounds());
        assertEquals(2, workout.getExerciseIds().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullId_throwsException() {
        new Workout(null, "Name", 3, List.of("e1"), List.of(30), List.of(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyName_throwsException() {
        new Workout("w1", "", 3, List.of("e1"), List.of(30), List.of(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRounds_throwsException() {
        new Workout("w1", "Name", 0, List.of("e1"), List.of(30), List.of(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullExerciseList_throwsException() {
        new Workout("w1", "Name", 3, null, List.of(30), List.of(10));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listEncapsulation_preventsModification() {
        Workout workout = new Workout(
                "w1", "Name", 3,
                new ArrayList<>(List.of("e1")),
                new ArrayList<>(List.of(30)),
                new ArrayList<>(List.of(10))
        );

        workout.getExerciseIds().add("e2");
    }
}