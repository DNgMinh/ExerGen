package com.example.exergen.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.persistence.ExerciseRepositorySQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

/**
 * Integration test verifying the architectural seam between ExerciseService 
 * and the real SQLite database.
 */
@RunWith(AndroidJUnit4.class)
public class ExerciseIntegrationTest {
    private static final String TEST_DB_NAME = "ExerGen_test.db";

    private ExerciseService exerciseService;
    private Context context;


    @Before
    public void setUp() {
        EnumMapper mapper = new EnumMapper();
        context = ApplicationProvider.getApplicationContext();

        context.deleteDatabase(TEST_DB_NAME);

        ExerciseRepositorySQLite repo = new ExerciseRepositorySQLite(context, mapper, TEST_DB_NAME);
        
        // Seed database from real CSV assets
        repo.seedData();

        exerciseService = new ExerciseService(repo);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(TEST_DB_NAME);
    }

    @Test
    public void testFilterByConstraints_RetrievesFromRealDatabase() {
        // Based on the default exercises.csv, "Pushups" should be for "Chest" using "Bodyweight"
        List<Exercise> results = exerciseService.filterByConstraints(
                Arrays.asList(EquipmentType.BODYWEIGHT),
                Arrays.asList(MuscleGroup.CHEST)
        );

        assertNotNull(results);
        assertFalse("Should have found default bodyweight chest exercises", results.isEmpty());
        
        // Verify one of the returned results is actually correct from the DB
        boolean foundPushups = false;
        for (Exercise e : results) {
            if (e.getName().equalsIgnoreCase("Pushups")) {
                foundPushups = true;
                break;
            }
        }
        assertTrue("Pushups should have been found in the filtered result", foundPushups);
    }

    @Test
    public void testAddAndFilterCustomExercise_PersistsToRealDatabase() {
        String customId = "custom-ex-1";
        Exercise custom = new Exercise(
                customId,
                "Z-Press",
                Arrays.asList(MuscleGroup.SHOULDERS),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Seated shoulder press",
                4,
                List.of("img")
        );

        // Act: Use the service to add it (which uses the real SQL repo)
        exerciseService.addExercise(custom);

        // Assert: Filter for the new constraints and see if SQLite finds it
        List<Exercise> results = exerciseService.filterByConstraints(
                Arrays.asList(EquipmentType.DUMBBELLS),
                Arrays.asList(MuscleGroup.SHOULDERS)
        );

        boolean foundCustom = false;
        for (Exercise e : results) {
            if (e.getId().equals(customId)) {
                foundCustom = true;
                break;
            }
        }
        assertTrue("Custom exercise should be retrievable via filtering", foundCustom);
    }
}
