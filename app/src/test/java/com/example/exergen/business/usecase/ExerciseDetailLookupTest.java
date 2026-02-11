package com.example.exergen.business.usecase;

import com.example.exergen.business.model.Exercise;
import com.example.exergen.persistence.repository.ExerciseRepository;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExerciseDetailLookupTest {
    private static class FakeExerciseRepository implements ExerciseRepository {
        private final List<Exercise> exercises;

        private FakeExerciseRepository(List<Exercise> exercises) {
            this.exercises = List.copyOf(exercises);
        }

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
            return exercises;
        }

        @Override
        public List<Exercise> filterByEquipment(String equipment) {
            return List.of();
        }

        @Override
        public List<Exercise> filterByMuscleGroup(String muscle) {
            return List.of();
        }

        @Override
        public void addExercise(Exercise exercise) {
            // Not needed for these tests.
        }
    }

    @Test
    public void getExerciseByIdReturnsExerciseWhenPresent() {
        Exercise target = new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2);
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(List.of(target)));

        Exercise result = service.getExerciseById("e1");

        assertNotNull(result);
        assertEquals("Pushup", result.getName());
    }

    @Test
    public void getExerciseByIdReturnsNullWhenMissing() {
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(List.of()));

        Exercise result = service.getExerciseById("missing");

        assertNull(result);
    }
}
