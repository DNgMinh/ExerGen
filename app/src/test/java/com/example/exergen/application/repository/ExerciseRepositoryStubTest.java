package com.example.exergen.application.repository;

import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.model.Exercise;
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

        Exercise result = stub.getExerciseById("ex-1");

        assertNotNull(result);
        assertEquals("Pushup", result.getName());
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
                "ex-1",
                "Duplicate Pushup",
                List.of("Chest"),
                List.of("Bodyweight"),
                "desc",
                2,
                "placeholder.png");
        stub.insertExercise(duplicate);
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsEmptyFilter() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();
        stub.filterByEquipment("");
    }
}