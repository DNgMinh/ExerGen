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
        new Exercise(null, "Name", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyName_throwsException() {
        new Exercise("id_1", "", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullMuscleGroups_throwsException() {
        new Exercise("id_1", "Name", null, Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullEquipment_throwsException() {
        new Exercise("id_1", "Name", Arrays.asList(MuscleGroup.CHEST), null, "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeIntensity_throwsException() {
        new Exercise("id_1", "Name", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", -1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyImageName_throwsException() {
        new Exercise("id_1", "Name", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyId_throwsException() {
        new Exercise("", "Name", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullName_throwsException() {
        new Exercise("id_1", null, Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "placeholder");
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankImageName_throwsException() {
        new Exercise("id_1", "Name", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "   ");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listEncapsulation_preventsModification() {
        Exercise exercise = new Exercise(
                "ex_1", "Name", Arrays.asList(MuscleGroup.CHEST), Arrays.asList(EquipmentType.BODYWEIGHT), "Inst", 1, "placeholder"
        );

        exercise.getMuscleGroups().add(MuscleGroup.SHOULDERS);
    }
}