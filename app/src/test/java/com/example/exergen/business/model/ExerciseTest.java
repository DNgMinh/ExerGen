package com.example.exergen.business.model;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.Arrays;
import java.util.List;

public class ExerciseTest {

    @Test
    public void validExercise_createsSuccessfully() {
        Exercise exercise = new Exercise(
                "ex_1",
                "Pushup",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Keep back straight",
                3,
                List.of("ex_pushup/0.jpg", "ex_pushup/1.jpg")
        );

        // Asserts
        assertEquals("ex_1", exercise.getId());
        assertEquals("Pushup", exercise.getName());
        assertEquals(2, exercise.getMuscleGroups().size());
        assertEquals(1, exercise.getEquipment().size());
        assertEquals("Keep back straight", exercise.getInstructions());
        assertEquals(3, exercise.getIntensity());
        assertEquals(2, exercise.getImagePaths().size());
        assertEquals("ex_pushup/0.jpg", exercise.getImagePaths().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullId_throwsException() {
        new Exercise(null, "Name", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of("placeholder"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyName_throwsException() {
        new Exercise("id_1", "", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of("placeholder"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullMuscleGroups_throwsException() {
        new Exercise("id_1", "Name", null, List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of("placeholder"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullEquipment_throwsException() {
        new Exercise("id_1", "Name", List.of(MuscleGroup.CHEST), null, "Inst", 1, List.of("placeholder"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeIntensity_throwsException() {
        new Exercise("id_1", "Name", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", -1, List.of("placeholder"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullImagePaths_throwsException() {
        new Exercise("id_1", "Name", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyImagePaths_throwsException() {
        new Exercise("id_1", "Name", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyId_throwsException() {
        new Exercise("", "Name", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of("placeholder"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullName_throwsException() {
        new Exercise("id_1", null, List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of("placeholder"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listEncapsulation_preventsModification() {
        Exercise exercise = new Exercise(
                "ex_1", "Name", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Inst", 1, List.of("placeholder")
        );

        exercise.getMuscleGroups().add(MuscleGroup.SHOULDERS);
    }

    @Test
    public void matches_returnsTrueForMatchingMuscleAndEquipment() {
        Exercise exercise = new Exercise(
                "ex_1",
                "Pushup",
                List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                List.of(EquipmentType.BODYWEIGHT),
                "Keep back straight",
                3,
                List.of("ex_pushup/0.jpg"));

        boolean result = exercise.matches(
                List.of(EquipmentType.BODYWEIGHT),
                List.of(MuscleGroup.CHEST));

        assertTrue(result);
    }

    @Test
    public void matches_returnsFalseWhenNoTargetMusclesProvided() {
        Exercise exercise = new Exercise(
                "ex_1",
                "Pushup",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.BODYWEIGHT),
                "Keep back straight",
                3,
                List.of("ex_pushup/0.jpg"));

        assertFalse(exercise.matches(List.of(EquipmentType.BODYWEIGHT), List.of()));
        assertFalse(exercise.matches(List.of(EquipmentType.BODYWEIGHT), null));
    }

    @Test
    public void matches_returnsTrueWhenEquipmentFilterEmptyAndMuscleMatches() {
        Exercise exercise = new Exercise(
                "ex_1",
                "Pushup",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.BODYWEIGHT),
                "Keep back straight",
                3,
                List.of("ex_pushup/0.jpg"));

        assertTrue(exercise.matches(List.of(), List.of(MuscleGroup.CHEST)));
        assertTrue(exercise.matches(null, List.of(MuscleGroup.CHEST)));
    }

    @Test
    public void matches_returnsFalseWhenEquipmentDoesNotMatch() {
        Exercise exercise = new Exercise(
                "ex_1",
                "Pushup",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.BODYWEIGHT),
                "Keep back straight",
                3,
                List.of("ex_pushup/0.jpg"));

        boolean result = exercise.matches(
                List.of(EquipmentType.BARBELL),
                List.of(MuscleGroup.CHEST));

        assertFalse(result);
    }
}