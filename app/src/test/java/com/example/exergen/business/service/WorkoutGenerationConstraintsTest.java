package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class WorkoutGenerationConstraintsTest {

    // --- CONSTRUCTOR TESTS ---

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNullSelectedEquipment() {
        // Correct order: Muscles first, Equipment second
        new WorkoutGenerationConstraints(List.of("Chest"), null, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNullTargetMuscles() {
        // Correct order: Muscles first, Equipment second
        new WorkoutGenerationConstraints(null, List.of("Dumbbells"), 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsEmptyTargetMuscles() {
        new WorkoutGenerationConstraints(List.of(), List.of("Dumbbells"), 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNonPositiveDuration() {
        new WorkoutGenerationConstraints(List.of("Chest"), List.of("Dumbbells"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsBlankConstraintValues() {
        new WorkoutGenerationConstraints(List.of("Chest"), List.of(" "), 20);
    }

    @Test
    public void testConstructorTrimsValuesAndStoresDuration() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of(" Chest "), List.of(" Dumbbells "), 25);

        assertEquals(List.of(EquipmentType.DUMBBELLS), constraints.getSelectedEquipment());
        assertEquals(List.of(MuscleGroup.CHEST), constraints.getTargetMuscleGroups());
        assertEquals(25, constraints.getDesiredDurationMinutes());
    }

    // --- MATCHES EXERCISE TESTS ---

    @Test
    public void testMatchesExerciseReturnsTrueWhenEquipmentAndMuscleMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Chest"), List.of("Dumbbells"), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                List.of(EquipmentType.DUMBBELLS), // Fixed: Was accidentally BODYWEIGHT
                "Press dumbbells",
                3,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseReturnsFalseWhenMuscleDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Legs"), List.of("Dumbbells"), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                List.of(EquipmentType.DUMBBELLS),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseReturnsFalseWhenEquipmentDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Chest"), List.of("Barbell"), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                List.of(EquipmentType.DUMBBELLS),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseAllowsAnyEquipmentWhenNoEquipmentSelected() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Chest"), List.of(), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Bodyweight Pushup",
                List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                List.of(EquipmentType.BODYWEIGHT),
                "Pushup",
                2,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }
}
