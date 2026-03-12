package com.example.exergen.persistence;

import static org.junit.Assert.*;
import com.example.exergen.model.Workout;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class WorkoutRepositoryStubTest {

    private WorkoutRepositoryStub repository;

    @Before
    public void setUp() {
        repository = new WorkoutRepositoryStub();
    }

    @Test
    public void getAllWorkouts_returnsInitialSeedData() {
        List<Workout> results = repository.getAllWorkouts();
        // The stub has 2 workouts (w1, w2)
        assertEquals(2, results.size());
    }

    @Test
    public void getWorkoutById_returnsCorrectWorkout() {
        Workout result = repository.getWorkoutById("w1");
        assertNotNull(result);
        assertEquals("Beginner Full Body", result.getName());
    }

    @Test
    public void saveWorkout_addsNewWorkout() {
        Workout newW = new Workout("new-id", "New", 1, List.of("ex1"), List.of(30), List.of(10));
        repository.saveWorkout(newW);

        Workout retrieved = repository.getWorkoutById("new-id");
        assertNotNull(retrieved);
        assertEquals(3, repository.getAllWorkouts().size());
    }

    @Test
    public void deleteWorkout_removesWorkout() {
        repository.deleteWorkout("w1");
        assertNull(repository.getWorkoutById("w1"));
        assertEquals(1, repository.getAllWorkouts().size());
    }

    @Test
    public void deleteWorkout_handlesInvalidIds() {
        int initialSize = repository.getAllWorkouts().size();
        repository.deleteWorkout(null);
        repository.deleteWorkout("");
        repository.deleteWorkout("non-existent");
        assertEquals(initialSize, repository.getAllWorkouts().size());
    }
}
