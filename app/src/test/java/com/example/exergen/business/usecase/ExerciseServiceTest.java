package com.example.exergen.business.usecase;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.repository.IExerciseRepository;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExerciseServiceTest {
    private static class FakeExerciseRepository implements IExerciseRepository {
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
            if (equipment == null || equipment.trim().isEmpty()) {
                throw new IllegalArgumentException("Equipment required.");
            }

            String normalizedEquipment = equipment.trim();
            return exercises.stream()
                    .filter(exercise -> exercise.getEquipment().stream()
                            .anyMatch(currentEquipment -> currentEquipment.equalsIgnoreCase(normalizedEquipment)))
                    .toList();
        }

        @Override
        public List<Exercise> filterByMuscleGroup(String muscleGroup) {
            if (muscleGroup == null || muscleGroup.trim().isEmpty()) {
                throw new IllegalArgumentException("Muscle required.");
            }

            String normalizedMuscleGroup = muscleGroup.trim();
            return exercises.stream()
                    .filter(exercise -> exercise.getMuscleGroups().stream()
                            .anyMatch(currentMuscleGroup -> currentMuscleGroup.equalsIgnoreCase(normalizedMuscleGroup)))
                    .toList();
        }

        @Override
        public void insertExercise(Exercise exercise) {
        }

        @Override
        public void deleteExercise(String id) {
        }

        @Override
        public void seedData() {
        }
    }

    @Test
    public void getAllExercisesReturnsAllItems() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"),
                new Exercise("e2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.getAllExercises();

        assertEquals(2, result.size());
        assertEquals("e1", result.get(0).getId());
        assertEquals("e2", result.get(1).getId());
    }

    @Test
    public void getExerciseByIdReturnsCorrectExercise() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"),
                new Exercise("e2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        Exercise result = service.getExerciseById("e2");

        assertNotNull(result);
        assertEquals("Squat", result.getName());
    }

    @Test
    public void filterByEquipmentReturnsMatchingExercises() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"),
                new Exercise("e2", "Bench Press", List.of("Chest"), List.of("Barbell"), "", 3, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByEquipment("Barbell");

        assertEquals(1, result.size());
        assertEquals("Bench Press", result.get(0).getName());
    }

    @Test
    public void filterByEquipmentReturnsEmptyWhenNoMatches() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"),
                new Exercise("e2", "Bench Press", List.of("Chest"), List.of("Barbell"), "", 3, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByEquipment("Dumbbells");

        assertTrue(result.isEmpty());
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsNullInput() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));
        service.filterByEquipment(null);
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsBlankInput() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));
        service.filterByEquipment("   ");
    }

    @Test
    public void filterByMuscleGroupReturnsMatchingExercises() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"),
                new Exercise("e2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, "placeholder"));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByMuscleGroup("Legs");

        assertEquals(1, result.size());
        assertEquals("Squat", result.get(0).getName());
    }
}