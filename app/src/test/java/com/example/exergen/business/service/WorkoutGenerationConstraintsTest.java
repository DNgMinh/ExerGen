package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

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
    public void testConstructorRejectsNonPositiveExerciseCount() {
        new WorkoutGenerationConstraints(List.of("Dumbbells"), List.of("Chest"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsBlankConstraintValues() {
        new WorkoutGenerationConstraints(List.of(" "), List.of("Chest"), 20);
    }

    @Test
    public void testConstructorTrimsValuesAndStoresExerciseCount() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of(" Dumbbells "), List.of(" Chest "), 25);

        assertEquals(List.of(EquipmentType.DUMBBELLS), constraints.getSelectedEquipment());
        assertEquals(List.of(MuscleGroup.CHEST), constraints.getTargetMuscleGroups());
        assertEquals(25, constraints.getTargetExerciseCount());
    }

    @Test
    public void testMatchesExerciseReturnsTrueWhenEquipmentAndMuscleMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Dumbbells"), List.of("Chest"), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.DUMBBELLS),
                "Press dumbbells",
                3,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseReturnsFalseWhenMuscleDoesNotMatch() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Dumbbells"), List.of("Legs"), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Dumbbell Press",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.DUMBBELLS),
                "Press dumbbells",
                3,
                List.of("img"));

        assertFalse(constraints.matchesExercise(exercise));
    }

    @Test
    public void testMatchesExerciseAllowsAnyEquipmentWhenNoEquipmentSelected() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of(), List.of("Chest"), 20);

        Exercise exercise = new Exercise(
                "ex-1",
                "Bodyweight Pushup",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.BODYWEIGHT),
                "Pushup",
                2,
                List.of("img"));

        assertTrue(constraints.matchesExercise(exercise));
    }
}