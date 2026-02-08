package com.example.exergen.business.usecase;

import com.example.exergen.business.model.Exercise;
import com.example.exergen.persistence.repository.ExerciseRepository;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExerciseServiceTest {
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
            List<Exercise> result = new ArrayList<>();
            for (Exercise exercise : exercises) {
                if (exercise.getEquipment().contains(equipment)) {
                    result.add(exercise);
                }
            }
            return result;
        }

        @Override
        public List<Exercise> filterByMuscleGroup(String muscle) {
            List<Exercise> result = new ArrayList<>();
            for (Exercise exercise : exercises) {
                if (exercise.getMuscleGroups().contains(muscle)) {
                    result.add(exercise);
                }
            }
            return result;
        }
    }

    @Test
    public void getAllExercisesReturnsAllItems() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2),
                new Exercise("e2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.getAllExercises();

        assertEquals(2, result.size());
        assertEquals("e1", result.get(0).getId());
        assertEquals("e2", result.get(1).getId());
    }

    @Test
    public void getExerciseByIdReturnsCorrectExercise() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2),
                new Exercise("e2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        Exercise result = service.getExerciseById("e2");

        assertNotNull(result);
        assertEquals("Squat", result.getName());
    }

    @Test
    public void filterByEquipmentReturnsMatchingExercises() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2),
                new Exercise("e2", "Bench Press", List.of("Chest"), List.of("Barbell"), "", 3));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByEquipment("Barbell");

        assertEquals(1, result.size());
        assertEquals("Bench Press", result.get(0).getName());
    }

    @Test
    public void filterByMuscleGroupReturnsMatchingExercises() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2),
                new Exercise("e2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByMuscleGroup("Legs");

        assertEquals(1, result.size());
        assertEquals("Squat", result.get(0).getName());
    }
}
