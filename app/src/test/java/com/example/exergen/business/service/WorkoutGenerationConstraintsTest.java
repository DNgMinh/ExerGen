package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.Exercise;

import org.junit.Test;

import java.util.List;

public class WorkoutGenerationConstraintsTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullSelectedEquipment() {
        new WorkoutGenerationConstraints(null, List.of("Chest"), 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullTargetMuscles() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), null, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsEmptyTargetMuscles() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), List.of(), 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNonPositiveDuration() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), List.of("Chest"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankConstraintValues() {
        new WorkoutGenerationConstraints(List.of(" "), List.of("Chest"), 20);
    }

    @Test
    public void constructorTrimsValuesAndStoresDuration() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(" Dumbbells "),
                List.of(" Chest "), 25);

        assertEquals(List.of("Dumbbells"), constraints.getSelectedEquipment());
        assertEquals(List.of("Chest"), constraints.getTargetMuscleGroups());
        assertEquals(25, constraints.getDesiredDurationMinutes());
    }

    @Test
    public void matchesExerciseReturnsTrueWhenEquipmentAndMuscleMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Chest"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of("Chest", "Triceps"),
                List.of("Dumbbells"),
                "Press dumbbells",
                3,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }

    @Test
    public void matchesExerciseReturnsFalseWhenMuscleDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Legs"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of("Chest", "Triceps"),
                List.of("Dumbbells"),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void matchesExerciseReturnsFalseWhenEquipmentDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Barbell"),
                List.of("Chest"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of("Chest", "Triceps"),
                List.of("Dumbbells"),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void matchesExerciseAllowsAnyEquipmentWhenNoEquipmentSelected() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(), List.of("Chest"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Bodyweight Pushup",
                List.of("Chest", "Triceps"),
                List.of("Bodyweight"),
                "Pushup",
                2,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }
}
