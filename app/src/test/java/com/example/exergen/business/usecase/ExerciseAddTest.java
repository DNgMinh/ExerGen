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
            return List.copyOf(exercises);
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
}
