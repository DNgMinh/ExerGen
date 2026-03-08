package com.example.exergen.business.usecase;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.persistence.WorkoutRepositoryStub;
import com.example.exergen.model.Workout;
import com.example.exergen.model.Exercise;
import java.util.List;
import java.util.ArrayList;

public class WorkoutUseCaseTest {

    private WorkoutUseCase workoutUseCase;
    private WorkoutRepositoryStub workoutRepository;

    @Before
    public void setUp() {
        // Create a list of exercises
        List<Exercise> exercises = List.of(
                new Exercise("pushups", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, "img"),
                new Exercise("squats", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, "img"),
                new Exercise("plank", "Plank", List.of("Core"), List.of("Bodyweight"), "", 2, "img"));

        ExerciseService exerciseService = new ExerciseService(new LocalFakeExerciseRepo(exercises));

        workoutRepository = new WorkoutRepositoryStub();
        workoutUseCase = new WorkoutUseCase(workoutRepository, exerciseService);
    }

    @Test
    public void testGetWorkoutById_Valid() {
        Workout result = workoutUseCase.getWorkoutById("w1");
        assertNotNull(result);
        assertEquals("Beginner Full Body", result.getName());
    }

    @Test
    public void testGetWorkoutById_Invalid() {
        assertNull(workoutUseCase.getWorkoutById(null));
        assertNull(workoutUseCase.getWorkoutById(""));
        assertNull(workoutUseCase.getWorkoutById("non-existent"));
    }

    @Test
    public void testGetExercisesForWorkout_ResolvesCorrectly() {
        Workout workout = workoutUseCase.getWorkoutById("w1");
        List<Exercise> exercises = workoutUseCase.getExercisesForWorkout(workout);

        assertNotNull(exercises);
        assertFalse("List should not be empty", exercises.isEmpty());
        assertEquals("Pushup", exercises.get(0).getName());
    }

    @Test
    public void testGetExercisesForWorkout_HandlesMissingExercises() {
        Workout brokenWorkout = new Workout("b1", "Broken", 1,
                List.of("fake-id"), List.of(30), List.of(10));

        List<Exercise> results = workoutUseCase.getExercisesForWorkout(brokenWorkout);
        assertTrue(results.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveWorkout_ThrowsOnNull() {
        workoutUseCase.saveWorkout(null);
    }

    @Test
    public void testGetExercisesForWorkout_PartialResolution() {
        // Workout with one valid ID ("pushups") and one non-existent ID
        Workout mixedWorkout = new Workout("m1", "Mixed Results", 1,
                List.of("pushups", "non-existent-id"), List.of(30, 30), List.of(10, 10));

        List<Exercise> results = workoutUseCase.getExercisesForWorkout(mixedWorkout);

        assertEquals(1, results.size());
        assertEquals("Pushup", results.get(0).getName());
    }

    @Test
    public void testGetExercisesForWorkout_NoMatchingExercisesInRepo() {
        // Create a valid workout with one ID
        Workout validWorkout = new Workout("w_none", "Valid But Missing", 1,
                List.of("ghost-id"), List.of(30), List.of(10));

        List<Exercise> results = workoutUseCase.getExercisesForWorkout(validWorkout);

        // Verify it returns an empty list
        assertNotNull(results);
        assertTrue("Should be empty because the ID wasn't found", results.isEmpty());
    }

    private static class LocalFakeExerciseRepo implements IExerciseRepository {
        private final List<Exercise> exercises;

        public LocalFakeExerciseRepo(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public Exercise getExerciseById(String id) {
            for (Exercise e : exercises) {
                if (e.getId().equals(id))
                    return e;
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
        public List<Exercise> filterByMuscleGroup(String muscleGroup) {
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
}