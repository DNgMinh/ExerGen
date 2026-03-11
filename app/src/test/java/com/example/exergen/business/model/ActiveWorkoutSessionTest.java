package com.example.exergen.business.model;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.WorkoutSet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;


public class ActiveWorkoutSessionTest {

    private Exercise dummyExercise;
    private WorkoutSet set1;
    private WorkoutSet set2;
    private WorkoutSet set3;

    @Before
    public void setUp() {
        dummyExercise = new Exercise(
                "ex_1",
                "Squat",
                Arrays.asList(MuscleGroup.LEGS),
                Arrays.asList(EquipmentType.BARBELL),
                "Squat down and stand up.",
                4,
                "ex_squat"
        );

        set1 = new WorkoutSet("set_1", dummyExercise, 10, 200.0, true);
        set2 = new WorkoutSet("set_2", dummyExercise, 5, 220.0, true);
        set3 = new WorkoutSet("set_3", dummyExercise, 10, 200.0, false);
    }


}
