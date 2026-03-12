package com.example.exergen.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;
import com.example.exergen.persistence.ExerciseRepositorySQLite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Integration test verifying the architectural seam between ExerciseService 
 * and the real SQLite database.
 */
@RunWith(AndroidJUnit4.class)
public class ExerciseIntegrationTest {

    private ExerciseService exerciseService;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        
        // Ensure clean state
        context.deleteDatabase("ExerGen.db");

        ExerciseRepositorySQLite repo = new ExerciseRepositorySQLite(context);
        
        // Seed database from real CSV assets
        repo.seedData();

        exerciseService = new ExerciseService(repo);
    }

    @Test
    public void testFilterByConstraints_RetrievesFromRealDatabase() {
        // Based on the default exercises.csv, "Pushups" should be for "Chest" using "Bodyweight"
        List<Exercise> results = exerciseService.filterByConstraints(
                List.of("Bodyweight"), 
                List.of("Chest")
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
                List.of("Shoulders"),
                List.of("Dumbbells"),
                "Seated shoulder press",
                4,
                List.of("img")
        );

        // Act: Use the service to add it (which uses the real SQL repo)
        exerciseService.addExercise(custom);

        // Assert: Filter for the new constraints and see if SQLite finds it
        List<Exercise> results = exerciseService.filterByConstraints(
                List.of("Dumbbells"), 
                List.of("Shoulders")
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
