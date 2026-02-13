package com.example.exergen.business.usecase;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.persistence.repository.ExerciseRepository;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExerciseAddTest {
    private static class FakeExerciseRepository implements ExerciseRepository {
        private final List<Exercise> exercises = new ArrayList<>();

        @Override
        public Exercise getExerciseById(String id) {
            for (Exercise exercise : exercises) {
                if (exercise.getId().equals(id)) {
                    return exercise;
                }
            }
            return null;
        }

        @Override
        public List<Exercise> getAllExercises() {
            return new ArrayList<>(exercises); // Return a copy to prevent modification
        }

        @Override
        public List<Exercise> filterByEquipment(String equipment) {
            // Not needed for these tests, can be left empty
            return List.of();
        }

        @Override
        public List<Exercise> filterByMuscleGroup(String muscle) {
            // Not needed for these tests, can be left empty
            return List.of();
        }

        @Override
        public void addExercise(Exercise exercise) {
            // To support the duplicate check test, we first remove any existing
            // exercise with the same ID before adding the new one.
            // This mimics how a real database 'upsert' or 'replace' might work.
            exercises.removeIf(e -> e.getId().equals(exercise.getId()));
            exercises.add(exercise);
        }
    }

    @Test
    public void addExerciseMakesExerciseRetrievable() {
        ExerciseService service = new ExerciseService(new FakeExerciseRepository());
        Exercise added = new Exercise("ex-99", "Burpee", List.of("Full Body"), List.of("Bodyweight"), "", 3);

        service.addExercise(added);

        Exercise result = service.getExerciseById("ex-99");
        assertNotNull(result);
        assertEquals("Burpee", result.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addExerciseRejectsNullInput() {
        ExerciseService service = new ExerciseService(new FakeExerciseRepository());

        service.addExercise(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addExerciseRejectsDuplicateId() {
        // Arrange
        ExerciseRepository fakeRepository = new FakeExerciseRepository();
        ExerciseService service = new ExerciseService(fakeRepository);
        Exercise existingExercise = new Exercise("ex-100", "Push-up", List.of("Chest"), List.of("Bodyweight"), "", 3);

        // Pre-load an exercise directly into the repo to simulate it already existing
        fakeRepository.addExercise(existingExercise);

        // Act
        // Attempt to add another exercise with the same ID via the service
        Exercise duplicateExercise = new Exercise("ex-100", "Diamond Push-up", List.of("Triceps"), List.of("Bodyweight"), "", 4);
        service.addExercise(duplicateExercise);

        // Assert
        // The test will pass if an IllegalArgumentException is thrown by the service.
    }
}
