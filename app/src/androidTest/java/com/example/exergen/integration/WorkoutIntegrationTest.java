package com.example.exergen.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;
import com.example.exergen.application.helper.ExerciseRepositorySQLite;
import com.example.exergen.application.helper.WorkoutRepositorySQLite;

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

        IEnumMapper enumMapper = new EnumMapper();
        WorkoutRepositorySQLite workoutRepo = new WorkoutRepositorySQLite(context, TEST_DB_NAME);
        ExerciseRepositorySQLite exerciseRepo = new ExerciseRepositorySQLite(context, TEST_DB_NAME, enumMapper);
        
        // Manually insert controlled test data to avoid slow asset-based seeding
        exerciseRepo.insertExercise(new Exercise("ex_1", "Test Exercise 1", 
                List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT), "Desc", 2, List.of("img")));
        exerciseRepo.insertExercise(new Exercise("ex_2", "Test Exercise 2", 
                List.of(MuscleGroup.LEGS), List.of(EquipmentType.BARBELL), "Desc", 3, List.of("img")));

        workoutRepo.saveWorkout(new Workout("w1", "Beginner Full Body", 3, 
                List.of(new WorkoutStep("ex_1", 30, 15), new WorkoutStep("ex_2", 45, 15))));
        workoutRepo.saveWorkout(new Workout("w2", "Upper Body Blast", 4, 
                List.of(new WorkoutStep("ex_1", 45, 20))));

        ExerciseService exerciseService = new ExerciseService(exerciseRepo);
        workoutUseCase = new WorkoutUseCase(workoutRepo, exerciseService);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(TEST_DB_NAME);
    }

    @Test
    public void testGetWorkoutById_RetrievesFromRealSQLite() {
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
                List.of(new WorkoutStep("ex_1", 30, 10), new WorkoutStep("ex_2", 30, 10))
        );

        workoutUseCase.saveWorkout(newWorkout);
        
        Workout retrieved = workoutUseCase.getWorkoutById(workoutId);
        assertNotNull(retrieved);
        assertEquals("Integration Test Workout", retrieved.getName());
        assertEquals(2, retrieved.getSets());
    }

    @Test
    public void testDeleteWorkout_RemovesFromRealSQLite() {
        String workoutId = "w1";
        assertNotNull(workoutUseCase.getWorkoutById(workoutId));

        workoutUseCase.deleteWorkout(workoutId);
        
        assertNull(workoutUseCase.getWorkoutById(workoutId));
    }

    @Test
    public void testGetAllWorkouts_ReturnsSeededDefaults() {
        List<Workout> workouts = workoutUseCase.getAllWorkouts();
        assertNotNull(workouts);
        assertEquals(2, workouts.size());
    }

    @Test
    public void testGetExercisesForWorkout_ResolvesExerciseDetails() {
        Workout workout = workoutUseCase.getWorkoutById("w1");
        assertNotNull(workout);

        List<com.example.exergen.model.Exercise> exercises = workoutUseCase.getExercisesForWorkout(workout);
        assertNotNull(exercises);
        assertEquals(workout.getSteps().size(), exercises.size());
    }
}
