package com.example.exergen.business.model;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.WorkoutSet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class WorkoutSetTest {

    private Exercise dummyExercise;

    @Before
    public void setUp() {
        dummyExercise = new Exercise(
                "ex_1",
                "Bench Press",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Keep back straight",
                4,
                "ex_pushup"
        );
    }

    @Test
    public void validWorkoutSet_createsSuccessfully() {
        WorkoutSet workoutSet = new WorkoutSet(
                "ws_1",
                dummyExercise,
                8,
                146.4,
                false
        );

        assertEquals("ws_1", workoutSet.getId());
        assertEquals(dummyExercise, workoutSet.getExercise());
        assertEquals(8, workoutSet.getReps());
        assertEquals(146.4, workoutSet.getWeight(), 0.001);
        assertFalse(workoutSet.isCompleted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullId_throwsException() {
        new WorkoutSet(null, dummyExercise, 8, 146.4, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullExercise_throwsException() {
        new WorkoutSet("ws_1", null, 8, 146.4, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeReps_throwsException() {
        new WorkoutSet("ws_1", dummyExercise, -1, 146.4, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeWeight_throwsException() {
        new WorkoutSet("ws_1", dummyExercise, 8, -0.1, false);
    }

    @Test
    public void testSettersUpdateValuesCorrectly() {
        WorkoutSet set = new WorkoutSet("set_1", dummyExercise, 10, 135.5, false);

        set.setReps(8);
        set.setWeight(150.5);
        set.setCompleted(true);

        assertEquals(8, set.getReps());
        assertEquals(150.5, set.getWeight(), 0.001);
        assertTrue(set.isCompleted());
    }
}
