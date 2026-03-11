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

public class WorkoutUseCaseTest {

    private WorkoutUseCase workoutUseCase;

    @Before
    public void setUp() {
        // Create a list of exercises with List<String> for imagePaths
        List<Exercise> exercises = List.of(
                new Exercise("pushups", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, List.of("img")),
                new Exercise("squats", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, List.of("img")),
                new Exercise("plank", "Plank", List.of("Core"), List.of("Bodyweight"), "", 2, List.of("img")));

        ExerciseService exerciseService = new ExerciseService(new LocalFakeExerciseRepo(exercises));

        WorkoutRepositoryStub workoutRepository = new WorkoutRepositoryStub();
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
    public void testGetAllWorkouts_ReturnsSeededWorkouts() {
        List<Workout> workouts = workoutUseCase.getAllWorkouts();
        assertNotNull(workouts);
        assertFalse(workouts.isEmpty());
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
    public void testSaveWorkout_PersistsWorkout() {
        Workout workout = new Workout("w-new", "New Workout", 1, List.of("pushups"), List.of(20), List.of(10));
        workoutUseCase.saveWorkout(workout);
        assertNotNull(workoutUseCase.getWorkoutById("w-new"));
    }

    @Test
    public void testDeleteWorkout_RemovesWorkout() {
        assertNotNull(workoutUseCase.getWorkoutById("w1"));
        workoutUseCase.deleteWorkout("w1");
        assertNull(workoutUseCase.getWorkoutById("w1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWorkout_RejectsBlankId() {
        workoutUseCase.deleteWorkout(" ");
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

    @Test
    public void testGetExercisesForWorkout_NullWorkoutReturnsEmptyList() {
        List<Exercise> results = workoutUseCase.getExercisesForWorkout(null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
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
