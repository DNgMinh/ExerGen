package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ExerciseEquipmentFilterTest {
    private ExerciseService service;

    @Before
    public void setUp() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "placeholder"),
                new Exercise("e2", "Bench Press", List.of("Chest"), List.of("Barbell"), "", 3, "placeholder"),
                new Exercise("e3", "Dumbbell Row", List.of("Back"), List.of("Dumbbells"), "", 3, "placeholder"));
        service = new ExerciseService(new FakeExerciseRepository(seed));
    }

    @Test
    public void filterByEquipmentIsCaseInsensitive() {
        List<Exercise> result = service.filterByEquipment("dumbbells");
        assertEquals(1, result.size());
        assertEquals("e3", result.get(0).getId());
    }

    @Test
    public void filterByEquipmentTrimsInput() {
        List<Exercise> result = service.filterByEquipment("  Barbell ");
        assertEquals(1, result.size());
        assertEquals("e2", result.get(0).getId());
    }

    @Test
    public void filterByEquipmentReturnsEmptyForUnknownEquipment() {
        List<Exercise> result = service.filterByEquipment("Kettlebell");
        assertTrue(result.isEmpty());
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsBlankInput() {
        service.filterByEquipment(" ");
    }

    private static class FakeExerciseRepository implements IExerciseRepository {
        private final List<Exercise> exercises;

        private FakeExerciseRepository(List<Exercise> exercises) {
            this.exercises = List.copyOf(exercises);
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
        public Exercise getExerciseById(String id) {
            for (Exercise exercise : exercises) {
                if (exercise.getId().equals(id)) {
                    return exercise;
                }
            }
            return null;
        }

        @Override
        public void deleteExercise(String id) {
        }

        @Override
        public void seedData() {
        }
    }
}
