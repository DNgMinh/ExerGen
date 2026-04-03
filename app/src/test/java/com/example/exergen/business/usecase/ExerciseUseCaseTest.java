package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExerciseUseCaseTest {

    private ExerciseUseCase exerciseUseCase;
    private ExerciseService mockService;

    private Exercise dumbbellExercise;
    private Exercise bodyweightExercise;

    @Before
    public void setUp() {
        mockService = mock(ExerciseService.class);
        exerciseUseCase = new ExerciseUseCase(mockService);

        // Prepare test data
        dumbbellExercise = new Exercise("1", "Dumbbell Curl",
                Collections.singletonList(MuscleGroup.BICEPS),
                Collections.singletonList(EquipmentType.DUMBBELLS),
                "Instructions", 3, Collections.singletonList("path/0.jpg"));

        bodyweightExercise = new Exercise("2", "Pushup",
                Collections.singletonList(MuscleGroup.CHEST),
                Collections.singletonList(EquipmentType.BODYWEIGHT),
                "Instructions", 2, Collections.singletonList("path/1.jpg"));

        when(mockService.getAllExercises()).thenReturn(Arrays.asList(dumbbellExercise, bodyweightExercise));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullService_ThrowsException() {
        new ExerciseUseCase(null);
    }

    @Test
    public void testGetInitialFilters_ReturnsAllEquipment() {
        List<EquipmentType> filters = exerciseUseCase.getEquipmentFilters();
        assertEquals("Should default to all equipment types",
                EquipmentType.values().length, filters.size());
    }

    @Test
    public void testSetEquipmentFilters_UpdatesState() {
        List<EquipmentType> newFilters = Collections.singletonList(EquipmentType.DUMBBELLS);
        exerciseUseCase.setEquipmentFilters(newFilters);

        List<EquipmentType> retrieved = exerciseUseCase.getEquipmentFilters();
        assertEquals(1, retrieved.size());
        assertTrue(retrieved.contains(EquipmentType.DUMBBELLS));
    }

    @Test
    public void testGetFilteredExercises_EquipmentFiltering() {
        // User only has dumbbells
        exerciseUseCase.setEquipmentFilters(Collections.singletonList(EquipmentType.DUMBBELLS));
        List<Exercise> results = exerciseUseCase.getFilteredExercises();

        assertEquals(1, results.size());
        assertEquals("Dumbbell Curl", results.get(0).getName());
    }

    @Test
    public void testGetFilteredExercises_EmptyEquipmentSelection_ReturnsEmptyList() {
        exerciseUseCase.setEquipmentFilters(new ArrayList<>());
        List<Exercise> results = exerciseUseCase.getFilteredExercises();
        assertTrue("Should return no exercises if no equipment is selected", results.isEmpty());
    }

    @Test
    public void testGetFilteredExercises_MuscleFiltering() {
        // Filter by Chest
        exerciseUseCase.setMuscleFilters(Collections.singletonList(MuscleGroup.CHEST));
        List<Exercise> results = exerciseUseCase.getFilteredExercises();

        assertEquals(1, results.size());
        assertEquals("Pushup", results.get(0).getName());
    }

    @Test
    public void testGetFilteredExercises_CombinedFiltering() {
        // Filter by Biceps AND Bodyweight (should return none)
        exerciseUseCase.setMuscleFilters(Collections.singletonList(MuscleGroup.BICEPS));
        exerciseUseCase.setEquipmentFilters(Collections.singletonList(EquipmentType.BODYWEIGHT));
        
        List<Exercise> results = exerciseUseCase.getFilteredExercises();
        assertTrue("Should return no exercises for mismatching filters", results.isEmpty());
    }

    @Test
    public void testGetExerciseById_PassesThroughToService() {
        when(mockService.getExerciseById("1")).thenReturn(dumbbellExercise);
        Exercise result = exerciseUseCase.getExerciseById("1");
        assertNotNull(result);
        assertEquals("Dumbbell Curl", result.getName());
        verify(mockService).getExerciseById("1");
    }
}
