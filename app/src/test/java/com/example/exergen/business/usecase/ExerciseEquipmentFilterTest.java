package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.persistence.repository.IExerciseRepository;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExerciseEquipmentFilterTest {
    private ExerciseService service;

    @Before
    public void setUp() {
        List<Exercise> seed = List.of(
                new Exercise("e1", "Pushup", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "", 2, List.of("placeholder")),
                new Exercise("e2", "Bench Press", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BARBELL), "", 3, List.of("placeholder")),
                new Exercise("e3", "Dumbbell Row", List.of(MuscleGroup.BACK), List.of(EquipmentType.DUMBBELLS), "", 3, List.of("placeholder")));
        service = new ExerciseService(new FakeExerciseRepository(seed));
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
