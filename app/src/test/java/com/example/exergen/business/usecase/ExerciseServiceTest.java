package com.example.exergen.business.usecase;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.persistence.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

import java.util.ArrayList;
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
        public List<Exercise> filterByEquipment(EquipmentType equipment) {
            if (equipment == null) {
                throw new InvalidFilterException("Equipment filter must be non-empty.");
            }

            List<Exercise> result = new ArrayList<>();
            for (Exercise exercise : exercises) {
                for (EquipmentType currentEquipment : exercise.getEquipment()) {
                    if (currentEquipment == equipment) {
                        result.add(exercise);
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public List<Exercise> filterByMuscleGroup(MuscleGroup muscle) {
            if (muscle == null) {
                throw new InvalidFilterException("Muscle group filter must be non-empty.");
            }

            List<Exercise> result = new ArrayList<>();
            for (Exercise exercise : exercises) {
                for (MuscleGroup currentMuscle : exercise.getMuscleGroups()) {
                    if (currentMuscle == muscle) {
                        result.add(exercise);
                        break;
                    }
                }
            }
            return result;
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
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")),
                new Exercise("e2", "Squat", List.of(MuscleGroup.LEGS), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.getAllExercises();

        assertEquals(2, result.size());
        assertEquals("e1", result.get(0).getId());
        assertEquals("e2", result.get(1).getId());
    }

    @Test
    public void getExerciseByIdReturnsCorrectExercise() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")),
                new Exercise("e2", "Squat", List.of(MuscleGroup.LEGS), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        Exercise result = service.getExerciseById("e2");

        assertNotNull(result);
        assertEquals("Squat", result.getName());
    }

    @Test
    public void filterByEquipmentReturnsMatchingExercises() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")),
                new Exercise("e2", "Bench Press", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BARBELL), "", 3, List.of("placeholder")));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByEquipment(EquipmentType.BARBELL);

        assertEquals(1, result.size());
        assertEquals("Bench Press", result.get(0).getName());
    }

    @Test
    public void filterByEquipmentReturnsEmptyWhenNoMatches() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")),
                new Exercise("e2", "Bench Press", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BARBELL), "", 3, List.of("placeholder")));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByEquipment(EquipmentType.DUMBBELLS);

        assertTrue(result.isEmpty());
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsNullInput() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));
        service.filterByEquipment(null);
    }


    @Test
    public void filterByMuscleGroupReturnsMatchingExercises() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")),
                new Exercise("e2", "Squat", List.of(MuscleGroup.LEGS), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")));
        ExerciseService service = new ExerciseService(new FakeExerciseRepository(seed));

        List<Exercise> result = service.filterByMuscleGroup(MuscleGroup.LEGS);

        assertEquals(1, result.size());
        assertEquals("Squat", result.get(0).getName());
    }
}
