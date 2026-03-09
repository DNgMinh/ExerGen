package com.example.exergen.persistence;

import static org.junit.Assert.*;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import com.example.exergen.model.Exercise;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, manifest = Config.NONE)

/*
 * Note! Robolectric opens a new database connection to test, but does not close
 * it, leading to
 * a string of "errors" from CloseGuard that there is some sort of memory leak.
 * This is expected,
 * and can be ignored since the database is properly maintained during regular
 * app use.
 */
public class ExerciseRepositorySQLiteTest {

    private ExerciseRepositorySQLite repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        repository = new ExerciseRepositorySQLite(context);

        // Manually seed one exercise to ensure DB isn't empty without using assets
        Exercise initial = new Exercise("test-id", "Bench Press", List.of("Chest"),
                List.of("Barbell"), "Instructions", 3, "img_name");
        repository.insertExercise(initial);
    }

    @Test
    public void testInsertAndGetExercise() {
        Exercise result = repository.getExerciseById("test-id");
        assertNotNull(result);
        assertEquals("Bench Press", result.getName());
    }

    @Test
    public void testGetAllExercises() {
        List<Exercise> list = repository.getAllExercises();
        assertFalse(list.isEmpty());
        assertTrue(list.size() >= 1);
    }

    @Test
    public void testDeleteExercise() {
        repository.deleteExercise("test-id");
        assertNull(repository.getExerciseById("test-id"));
    }
}