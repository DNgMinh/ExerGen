package com.example.exergen.business.service;

import static org.junit.Assert.*;
import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import org.junit.Before;
import org.junit.Test;

import com.example.exergen.persistence.ExerciseRepositoryStub;
import com.example.exergen.model.Exercise;
import java.util.List;

public class ExerciseServiceTest {

    private ExerciseService exerciseService;
    private ExerciseRepositoryStub exerciseRepository;

    @Before
    public void setUp() {
        // Initialize with the Stub to keep tests fast and independent of SQLite
        exerciseRepository = new ExerciseRepositoryStub();
        exerciseService = new ExerciseService(exerciseRepository);
    }

    @Test
    public void getAllExercises_returnsFullList() {
        List<Exercise> results = exerciseService.getAllExercises();
        // The stub now has 20 exercises from the CSV data
        assertEquals(20, results.size());
        assertEquals("Pushups", results.get(0).getName());
    }

    @Test
    public void getExerciseById_validId_returnsCorrectExercise() {
        Exercise result = exerciseService.getExerciseById("ex_1");
        assertNotNull(result);
        assertEquals("Pushups", result.getName());
    }

    @Test
    public void getExerciseById_nonExistentId_returnsNull() {
        // Testing a completely random ID that isn't in the stub
        Exercise result = exerciseService.getExerciseById("fake-id-999");
        assertNull(result);
    }

    @Test
    public void getExerciseById_nullId_returnsNull() {
        // Ensuring the service doesn't crash if passed a null ID
        Exercise result = exerciseService.getExerciseById(null);
        assertNull(result);
    }

    @Test
    public void getExerciseById_emptyId_returnsNull() {
        Exercise result = exerciseService.getExerciseById("");
        assertNull(result);
    }

    @Test
    public void serviceReflectsDeletedExercise() {
        // 1. Verify it exists
        assertNotNull(exerciseService.getExerciseById("ex_1"));

        // 2. Delete it through the repo
        exerciseRepository.deleteExercise("ex_1");

        // 3. Service should now return null
        assertNull(exerciseService.getExerciseById("ex_1"));
        assertEquals(19, exerciseService.getAllExercises().size());
    }

    @Test
    public void serviceReflectsNewExercise() {
        Exercise newEx = new Exercise(
                "new-01", "Handstand", List.of("Shoulders"),
                List.of("Bodyweight"), "Balance on hands", 5, List.of("placeholder"));

        exerciseRepository.insertExercise(newEx);

        Exercise retrieved = exerciseService.getExerciseById("new-01");
        assertNotNull(retrieved);
        assertEquals("Handstand", retrieved.getName());
    }

    @Test
    public void getAllExercises_emptyRepository_returnsEmptyList() {
        // Wipe the stub data
        List<Exercise> all = exerciseRepository.getAllExercises();
        for (Exercise e : all) {
            exerciseRepository.deleteExercise(e.getId());
        }

        List<Exercise> results = exerciseService.getAllExercises();
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullRepository() {
        new ExerciseService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addExerciseRejectsNullExercise() {
        exerciseService.addExercise(null);
    }

    @Test(expected = DuplicateExerciseException.class)
    public void addExerciseRejectsDuplicateId() {
        Exercise duplicate = new Exercise(
                "ex_1", "Duplicate Pushup", List.of("Chest"),
                List.of("Bodyweight"), "Duplicate", 2, List.of("placeholder"));
        exerciseService.addExercise(duplicate);
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsNullInput() {
        exerciseService.filterByEquipment(null);
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByEquipmentRejectsEmptyInput() {
        exerciseService.filterByEquipment("");
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByMuscleGroupRejectsNullInput() {
        exerciseService.filterByMuscleGroup(null);
    }

    @Test(expected = InvalidFilterException.class)
    public void filterByMuscleGroupRejectsEmptyInput() {
        exerciseService.filterByMuscleGroup("");
    }

    @Test
    public void filterByEquipmentReturnsEmptyWhenNoMatches() {
        List<Exercise> results = exerciseService.filterByEquipment("Kettlebell");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void filterByMuscleGroupReturnsEmptyWhenNoMatches() {
        List<Exercise> results = exerciseService.filterByMuscleGroup("Forearms");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
