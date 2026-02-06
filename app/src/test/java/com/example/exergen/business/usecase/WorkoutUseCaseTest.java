package com.example.exergen.business.usecase;

import com.example.exergen.business.model.Workout;
import com.example.exergen.persistence.repository.WorkoutRepository;

import org.junit.Test;

import java.util.List;
import java.util.HashMap;

import static org.junit.Assert.*;

public class WorkoutUseCaseTest {

    // Simple fake repository for testing
    private static class FakeWorkoutRepository implements WorkoutRepository {
        private final HashMap<String, Workout> store = new HashMap<>();

        @Override
        public void saveWorkout(Workout workout) {
            store.put(workout.getId(), workout);
        }

        @Override
        public Workout getWorkoutById(String workoutId) {
            return store.get(workoutId);
        }

        @Override
        public List<Workout> getAllWorkouts() {
            return List.copyOf(store.values());
        }
    }

    @Test
    public void testSaveAndLoadWorkout() {
        // Setup
        FakeWorkoutRepository repo = new FakeWorkoutRepository();
        WorkoutUseCase useCase = new WorkoutUseCase(repo);

        // Create workout
        Workout workout = new Workout(
            "w1",
            "Test Workout",
            2,
            List.of("pushups", "squats"),
            List.of(30, 40),
            List.of(15, 20)
        );

        // Save
        useCase.saveWorkout(workout);

        // Load
        Workout loaded = useCase.loadWorkout("w1");

        // Assert
        assertNotNull(loaded);
        assertEquals("w1", loaded.getId());
        assertEquals("Test Workout", loaded.getName());
        assertEquals(2, loaded.getRounds());
        assertEquals(List.of("pushups", "squats"), loaded.getExerciseIds());
    }

    @Test
    public void testGetAllWorkouts() {
        FakeWorkoutRepository repo = new FakeWorkoutRepository();
        WorkoutUseCase useCase = new WorkoutUseCase(repo);

        Workout w1 = new Workout("a", "A", 1,
                List.of("pushups"), List.of(30), List.of(15));

        Workout w2 = new Workout("b", "B", 1,
                List.of("squats"), List.of(45), List.of(20));

        useCase.saveWorkout(w1);
        useCase.saveWorkout(w2);

        List<Workout> all = useCase.getAllWorkouts();

        assertEquals(2, all.size());
    }
}
