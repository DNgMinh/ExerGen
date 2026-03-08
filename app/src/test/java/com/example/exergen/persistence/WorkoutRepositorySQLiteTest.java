package com.example.exergen.persistence;

import static org.junit.Assert.*;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import com.example.exergen.model.Workout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = { Build.VERSION_CODES.O_MR1 }, manifest = Config.NONE)
public class WorkoutRepositorySQLiteTest {

    private WorkoutRepositorySQLite repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        repository = new WorkoutRepositorySQLite(context);

        Workout initial = new Workout(
                "w1",
                "Beginner Full Body",
                2,
                List.of("pushups", "squats", "plank"),
                List.of(30, 45, 60),
                List.of(15, 20, 25));
        repository.saveWorkout(initial);
    }

    @Test
    public void testSaveAndGetWorkout() {
        Workout result = repository.getWorkoutById("w1");

        assertNotNull(result);
        assertEquals("Beginner Full Body", result.getName());
        assertEquals(2, result.getRounds());

        // Verify list serialization/deserialization for Strings
        assertEquals(3, result.getExerciseIds().size());
        assertEquals("squats", result.getExerciseIds().get(1));

        // Verify Integer list reconstruction
        assertEquals(Integer.valueOf(45), result.getWorkSeconds().get(1));
        assertEquals(Integer.valueOf(25), result.getRestSeconds().get(2));
    }

    @Test
    public void testGetAllWorkouts() {
        List<Workout> all = repository.getAllWorkouts();
        assertFalse(all.isEmpty());
        assertTrue(all.size() >= 1);
    }

    @Test
    public void testUpdateExistingWorkout() {
        Workout updated = new Workout(
                "w1",
                "Updated Name",
                5,
                List.of("pushups"),
                List.of(10),
                List.of(5));
        repository.saveWorkout(updated);

        Workout result = repository.getWorkoutById("w1");
        assertEquals("Updated Name", result.getName());
        assertEquals(5, result.getRounds());
        assertEquals(1, result.getExerciseIds().size());
    }

    @Test
    public void testGetInvalidIdReturnsNull() {
        assertNull(repository.getWorkoutById("non-existent"));
    }
}