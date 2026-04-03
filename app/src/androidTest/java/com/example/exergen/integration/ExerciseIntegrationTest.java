package com.example.exergen.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.application.helper.ExerciseRepositorySQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ExerciseIntegrationTest {
    private static final String TEST_DB_NAME = "ExerGen_test.db";

    private ExerciseService exerciseService;
    private Context context;
    private IEnumMapper enumMapper;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(TEST_DB_NAME);

        enumMapper = new EnumMapper();
        ExerciseRepositorySQLite repo = new ExerciseRepositorySQLite(context, TEST_DB_NAME, enumMapper);
        
        // Manually insert one test exercise instead of full asset-based seeding
        repo.insertExercise(new Exercise(
                "ex_1", "Pushups", 
                List.of(MuscleGroup.CHEST), 
                List.of(EquipmentType.BODYWEIGHT), 
                "Instructions", 2, List.of("img")
        ));

        exerciseService = new ExerciseService(repo);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(TEST_DB_NAME);
    }

    @Test
    public void testFilterByConstraints_RetrievesFromRealDatabase() {
        List<Exercise> results = exerciseService.filterByConstraints(
                List.of(EquipmentType.BODYWEIGHT),
                List.of(MuscleGroup.CHEST)
        );

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(e -> e.getName().equalsIgnoreCase("Pushups")));
    }

    @Test
    public void testGetExerciseById_RetrievesTestExercise() {
        Exercise exercise = exerciseService.getExerciseById("ex_1");
        assertNotNull("Manual test exercise should be retrievable by ID", exercise);
    }

    @Test
    public void testAddAndFilterCustomExercise_PersistsToRealDatabase() {
        String customId = "custom-ex-1";
        Exercise custom = new Exercise(
                customId, "Z-Press",
                List.of(MuscleGroup.SHOULDERS),
                List.of(EquipmentType.DUMBBELLS),
                "Seated shoulder press", 4, List.of("img")
        );

        exerciseService.addExercise(custom);

        List<Exercise> results = exerciseService.filterByConstraints(
                List.of(EquipmentType.DUMBBELLS),
                List.of(MuscleGroup.SHOULDERS)
        );

        assertTrue(results.stream().anyMatch(e -> e.getId().equals(customId)));
    }

    @Test
    public void testDeleteExercise_RemovesPreviouslyAddedExercise() {
        String id = "ex_1";
        assertNotNull(exerciseService.getExerciseById(id));
        
        exerciseService.deleteExercise(id);
        assertNull("Deleted exercise should not be found", exerciseService.getExerciseById(id));
    }
}
