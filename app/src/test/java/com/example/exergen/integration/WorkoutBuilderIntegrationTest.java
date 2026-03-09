package com.example.exergen.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.persistence.ExerciseRepositorySQLite;
import com.example.exergen.persistence.WorkoutRepositorySQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = { Build.VERSION_CODES.O_MR1 }, manifest = Config.NONE)
public class WorkoutBuilderIntegrationTest {
    private static final String TEST_DB_NAME = "ExerGen.db";

    private Context context;
    private WorkoutBuilderUseCase workoutBuilderUseCase;
    private WorkoutUseCase workoutUseCase;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(TEST_DB_NAME);

        ExerciseRepositorySQLite exerciseRepository = new ExerciseRepositorySQLite(context);
        WorkoutRepositorySQLite workoutRepository = new WorkoutRepositorySQLite(context);
        ExerciseService exerciseService = new ExerciseService(exerciseRepository);

        seedExerciseData(exerciseRepository);

        workoutBuilderUseCase = new WorkoutBuilderUseCase(exerciseService);
        workoutUseCase = new WorkoutUseCase(workoutRepository, exerciseService);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(TEST_DB_NAME);
    }

    @Test
    public void generatedWorkoutCanBeSavedAndLoadedFromSQLite() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Chest"), 3);

        Workout generated = workoutBuilderUseCase.generateWorkout(constraints);
        workoutUseCase.saveWorkout(generated);
        Workout loaded = workoutUseCase.getWorkoutById(generated.getId());

        assertNotNull(loaded);
        assertEquals(generated.getId(), loaded.getId());
        assertEquals("Generated Workout", loaded.getName());
        assertTrue(loaded.getExerciseIds().size() > 0);
        assertEquals(loaded.getExerciseIds().size(), loaded.getWorkSeconds().size());
        assertEquals(loaded.getExerciseIds().size(), loaded.getRestSeconds().size());
    }

    @Test
    public void multipleGeneratedWorkoutsAreSavedWithoutOverwrite() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"),
                List.of("Chest"), 2);

        Workout generatedOne = workoutBuilderUseCase.generateWorkout(constraints);
        Workout generatedTwo = workoutBuilderUseCase.generateWorkout(constraints);

        workoutUseCase.saveWorkout(generatedOne);
        workoutUseCase.saveWorkout(generatedTwo);

        Workout loadedOne = workoutUseCase.getWorkoutById(generatedOne.getId());
        Workout loadedTwo = workoutUseCase.getWorkoutById(generatedTwo.getId());

        assertNotNull(loadedOne);
        assertNotNull(loadedTwo);
        assertTrue(!generatedOne.getId().equals(generatedTwo.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void generationThrowsWhenNoExerciseMatchesSQLiteData() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Barbell"),
                List.of("Shoulders"), 2);
        workoutBuilderUseCase.generateWorkout(constraints);
    }

    private void seedExerciseData(ExerciseRepositorySQLite exerciseRepository) {
        exerciseRepository.insertExercise(new Exercise(
                "it2-ex-1", "Dumbbell Press", List.of("Chest"), List.of("Dumbbells"), "", 3, "img"));
        exerciseRepository.insertExercise(new Exercise(
                "it2-ex-2", "Bodyweight Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, "img"));
    }
}
