package com.example.exergen.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class ExerciseTest {

    @Test
    public void validExercise_createsSuccessfully() {
        Exercise exercise = new Exercise(
                "ex_1",
                "Pushup",
                List.of("Chest", "Triceps"),
                List.of("Bodyweight"),
                "Keep back straight",
                3,
                "ex_pushup"
        );

        // Asserts
        assertEquals("ex_1", exercise.getId());
        assertEquals("Pushup", exercise.getName());
        assertEquals(2, exercise.getMuscleGroups().size());
        assertEquals(1, exercise.getEquipment().size());
        assertEquals("Keep back straight", exercise.getInstructions());
        assertEquals(3, exercise.getIntensity());
        assertEquals("ex_pushup", exercise.getImageName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullId_throwsException() {
        new Exercise(null, "Name", List.of("Chest"), List.of("Bodyweight"), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyName_throwsException() {
        new Exercise("id_1", "", List.of("Chest"), List.of("Bodyweight"), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullMuscleGroups_throwsException() {
        new Exercise("id_1", "Name", null, List.of("Bodyweight"), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullEquipment_throwsException() {
        new Exercise("id_1", "Name", List.of("Chest"), null, "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeIntensity_throwsException() {
        new Exercise("id_1", "Name", List.of("Chest"), List.of("Bodyweight"), "Inst", -1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyImageName_throwsException() {
        new Exercise("id_1", "Name", List.of("Chest"), List.of("Bodyweight"), "Inst", 1, "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listEncapsulation_preventsModification() {
        Exercise exercise = new Exercise(
                "ex_1", "Name", List.of("Chest"), List.of("Bodyweight"), "Inst", 1, "placeholder"
        );

        exercise.getMuscleGroups().add("Shoulders");
    }
}