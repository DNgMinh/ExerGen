package com.example.exergen.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Workout;
import com.example.exergen.persistence.ExerciseRepositorySQLite;
import com.example.exergen.persistence.WorkoutRepositorySQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class WorkoutIntegrationTest {
    private static final String TEST_DB_NAME = "ExerGen_test.db";

    private WorkoutUseCase workoutUseCase;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        context.deleteDatabase(TEST_DB_NAME);

        WorkoutRepositorySQLite workoutRepo = new WorkoutRepositorySQLite(context, TEST_DB_NAME);
        ExerciseRepositorySQLite exerciseRepo = new ExerciseRepositorySQLite(context, TEST_DB_NAME);
        
        // Seed database with default content as required
        workoutRepo.seedData();
        exerciseRepo.seedData();

        ExerciseService exerciseService = new ExerciseService(exerciseRepo);
        workoutUseCase = new WorkoutUseCase(workoutRepo, exerciseService);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(TEST_DB_NAME);
    }

    @Test
    public void testGetWorkoutById_RetrievesFromRealSQLite() {
        // "w1" is expected to be in the seeded data
        Workout workout = workoutUseCase.getWorkoutById("w1");
        
        assertNotNull(workout);
        assertEquals("w1", workout.getId());
        assertEquals("Beginner Full Body", workout.getName());
    }

    @Test
    public void testSaveAndRetrieveNewWorkout_PersistsToRealSQLite() {
        String workoutId = "it-workout-save";
        Workout newWorkout = new Workout(
                workoutId,
                "Integration Test Workout",
                2,
                List.of("ex_1", "ex_2"),
                List.of(30, 30),
                List.of(10, 10)
        );

        workoutUseCase.saveWorkout(newWorkout);
        
        Workout retrieved = workoutUseCase.getWorkoutById(workoutId);
        assertNotNull(retrieved);
        assertEquals("Integration Test Workout", retrieved.getName());
        assertEquals(2, retrieved.getRounds());
    }

    @Test
    public void testDeleteWorkout_RemovesFromRealSQLite() {
        String workoutId = "w1";
        assertNotNull(workoutUseCase.getWorkoutById(workoutId));

        workoutUseCase.deleteWorkout(workoutId);
        
        assertNull(workoutUseCase.getWorkoutById(workoutId));
    }
}
