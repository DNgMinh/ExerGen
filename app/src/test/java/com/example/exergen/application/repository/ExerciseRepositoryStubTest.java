package com.example.exergen.application.repository;

import com.example.exergen.model.Exercise;

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

    @Test
    public void filterByEquipmentReturnsMatchingExercises() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();

        List<Exercise> result = stub.filterByEquipment("Dumbbells");

        assertFalse(result.isEmpty());
        for (Exercise exercise : result) {
            assertTrue(exercise.getEquipment().contains("Dumbbells"));
        }
    }

    @Test
    public void filterByMuscleGroupReturnsMatchingExercises() {
        ExerciseRepositoryStub stub = new ExerciseRepositoryStub();

        List<Exercise> result = stub.filterByMuscleGroup("Legs");

        assertFalse(result.isEmpty());
        for (Exercise exercise : result) {
            assertTrue(exercise.getMuscleGroups().contains("Legs"));
        }
    }
}
