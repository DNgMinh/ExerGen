package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class WorkoutBuilderUseCaseTest {

    private WorkoutBuilderUseCase workoutBuilderUseCase;

    @Before
    public void setUp() {
        List<Exercise> exercises = List.of(
                new Exercise("ex-chest-db", "Dumbbell Press", List.of("Chest"), List.of("Dumbbells"), "", 3, "img"),
                new Exercise("ex-legs-bw", "Air Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, "img"),
                new Exercise("ex-back-bar", "Barbell Row", List.of("Back"), List.of("Barbell"), "", 3, "img"));

        ExerciseService exerciseService = new ExerciseService(new FakeExerciseRepository(exercises));
        workoutBuilderUseCase = new WorkoutBuilderUseCase(exerciseService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateWorkoutRejectsNullConstraints() {
        workoutBuilderUseCase.generateWorkout(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateWorkoutRejectsNoMatchingExercises() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Kettlebell"),
                List.of("Shoulders"), 10);
        workoutBuilderUseCase.generateWorkout(constraints);
    }

    @Test
    public void generateWorkoutBuildsRoutineForDurationAndConstraints() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Chest"), 3);

        Workout result = workoutBuilderUseCase.generateWorkout(constraints);

        assertEquals("Generated Workout", result.getName());
        assertEquals(1, result.getRounds());
        assertEquals(3, result.getExerciseIds().size());
        for (String id : result.getExerciseIds()) {
            assertEquals("ex-chest-db", id);
        }
        for (Integer work : result.getWorkSeconds()) {
            assertEquals(45, work.intValue());
        }
        for (Integer rest : result.getRestSeconds()) {
            assertEquals(15, rest.intValue());
        }
    }

    @Test
    public void generateWorkoutAllowsEmptyEquipmentSelection() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(), List.of("Legs"), 2);

        Workout result = workoutBuilderUseCase.generateWorkout(constraints);

        assertEquals(2, result.getExerciseIds().size());
        assertTrue(result.getExerciseIds().contains("ex-legs-bw"));
    }

    @Test
    public void generateWorkoutAssignsUniqueIdsAcrossGenerations() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Chest"), 2);

        Workout first = workoutBuilderUseCase.generateWorkout(constraints);
        Workout second = workoutBuilderUseCase.generateWorkout(constraints);

        assertTrue(first.getId().startsWith("generated-"));
        assertTrue(second.getId().startsWith("generated-"));
        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    public void generateWorkoutCyclesMatchingExercisesWhenDurationRequiresMoreSlots() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(),
                List.of("Chest", "Legs"), 5);

        Workout result = workoutBuilderUseCase.generateWorkout(constraints);

        assertEquals(5, result.getExerciseIds().size());
        assertTrue(result.getExerciseIds().contains("ex-chest-db"));
        assertTrue(result.getExerciseIds().contains("ex-legs-bw"));
    }

    private static class FakeExerciseRepository implements IExerciseRepository {
        private final List<Exercise> exercises;

        private FakeExerciseRepository(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public List<Exercise> getAllExercises() {
            return exercises;
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
