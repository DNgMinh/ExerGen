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

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNullSelectedEquipment() {
        new WorkoutGenerationConstraints(null, List.of("Chest"), 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNullTargetMuscles() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), null, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsEmptyTargetMuscles() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), List.of(), 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNonPositiveDuration() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), List.of("Chest"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsBlankConstraintValues() {
        new WorkoutGenerationConstraints(List.of(" "), List.of("Chest"), 20);
    }

    @Test
    public void testConstructorTrimsValuesAndStoresDuration() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(" Dumbbells "),
                List.of(" Chest "), 25);

        assertEquals(Arrays.asList(EquipmentType.DUMBBELLS), constraints.getSelectedEquipment());
        assertEquals(Arrays.asList(MuscleGroup.CHEST), constraints.getTargetMuscleGroups());
        assertEquals(25, constraints.getDesiredDurationMinutes());
    }

    @Test
    public void testMatchesExerciseReturnsTrueWhenEquipmentAndMuscleMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Chest"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Press dumbbells",
                3,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseReturnsFalseWhenMuscleDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Legs"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseReturnsFalseWhenEquipmentDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Barbell"),
                List.of("Chest"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseAllowsAnyEquipmentWhenNoEquipmentSelected() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(), List.of("Chest"), 20);
        Exercise exercise = new Exercise(
                "ex-1",
                "Bodyweight Pushup",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Pushup",
                2,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }
}
