package com.example.exergen.business.usecase;

import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExerciseAddTest {
    private static class FakeExerciseRepository implements IExerciseRepository {
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
            return new ArrayList<>(exercises);
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
            exercises.removeIf(e -> e.getId().equals(exercise.getId()));
            exercises.add(exercise);
        }

        @Override
        public void deleteExercise(String id) {
        }

        @Override
        public void seedData() {
        }
    }

    @Test
    public void addExerciseMakesExerciseRetrievable() {
        ExerciseService service = new ExerciseService(new FakeExerciseRepository());
        Exercise added = new Exercise("ex-99", "Burpee", List.of(MuscleGroup.FULL_BODY), List.of(EquipmentType.BODYWEIGHT), "", 3,
                List.of("placeholder"));

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

    @Test(expected = DuplicateExerciseException.class)
    public void addExerciseRejectsDuplicateId() {
        IExerciseRepository fakeRepository = new FakeExerciseRepository();
        ExerciseService service = new ExerciseService(fakeRepository);
        Exercise existingExercise = new Exercise("ex-100", "Push-up", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 3,
                List.of("placeholder"));

        fakeRepository.insertExercise(existingExercise);

        Exercise duplicateExercise = new Exercise("ex-100", "Diamond Push-up", List.of(MuscleGroup.TRICEPS),
                List.of(EquipmentType.BODYWEIGHT), "", 4, List.of("placeholder"));
        service.addExercise(duplicateExercise);
    }
}
