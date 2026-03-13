package com.example.exergen.application.repository;

import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.persistence.ExerciseRepositoryStub;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExerciseRepositoryStubTest {
    @Test
    public void getAllExercisesReturnsSeedData() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();

        List<Exercise> result = stub.getAllExercises();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getExerciseByIdReturnsCorrectExercise() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();

        Exercise result = stub.getExerciseById("ex_1");

        assertNotNull(result);
        assertEquals("Pushups", result.getName());
    }

    @Test
    public void getExerciseByIdReturnsNullWhenMissing() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();

        Exercise result = stub.getExerciseById("missing");

        assertNull(result);
    }

    @Test(expected = DuplicateExerciseException.class)
    public void insertExerciseThrowsOnDuplicateId() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();
        Exercise duplicate = new Exercise(
                "ex_1",
                "Duplicate Pushup",
                List.of(MuscleGroup.CHEST),
                List.of(EquipmentType.BODYWEIGHT),
                "desc",
                2,
                List.of("placeholder.png"));
        stub.insertExercise(duplicate);
    }
}
