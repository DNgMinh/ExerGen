package com.example.exergen.business.usecase;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExerciseDetailLookupTest {
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
            return List.of();
        }

        @Override
        public List<Exercise> filterByMuscleGroup(MuscleGroup muscleGroup) {
            return List.of();
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
    public void getExerciseByIdReturnsExerciseWhenPresent() {
        Exercise target = new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder"));
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
