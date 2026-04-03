package com.example.exergen.business.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.Exercise;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorkoutGenerationConstraintsTest {

    private IEnumMapper mockMapper;

    @Before
    public void setUp() {
        mockMapper = mock(IEnumMapper.class);
        when(mockMapper.toEquipmentEnums(anyList())).thenReturn(Collections.singletonList(EquipmentType.DUMBBELLS));
        when(mockMapper.toMuscleEnums(anyList())).thenReturn(Collections.singletonList(MuscleGroup.CHEST));
    }

    @Test
    public void testCreateCountBased_ValidInput_Success() {
        WorkoutGenerationConstraints constraints = WorkoutGenerationConstraints.createCountBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 5, 30, 15);
        
        assertEquals(5, constraints.getTargetExerciseCount());
        assertEquals(30, constraints.getWorkSeconds());
        assertEquals(15, constraints.getRestSeconds());
        assertFalse(constraints.isTimeBased());
    }

    @Test
    public void testCreateTimeBased_ValidInput_Success() {
        WorkoutGenerationConstraints constraints = WorkoutGenerationConstraints.createTimeBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 600, 30, 15);
        
        assertEquals(600, constraints.getTargetDurationSeconds());
        assertTrue(constraints.isTimeBased());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_InvalidExerciseCount_ThrowsException() {
        WorkoutGenerationConstraints.createCountBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 0, 30, 15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_InvalidDuration_ThrowsException() {
        WorkoutGenerationConstraints.createTimeBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 0, 30, 15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_InvalidWorkSeconds_ThrowsException() {
        WorkoutGenerationConstraints.createCountBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 5, 0, 15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_InvalidRestSeconds_ThrowsException() {
        WorkoutGenerationConstraints.createCountBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 5, 30, -1);
    }

    @Test
    public void testMatchesExercise_CallsMatcher() {
        WorkoutGenerationConstraints constraints = WorkoutGenerationConstraints.createCountBased(
                mockMapper, Arrays.asList("Dumbbells"), Arrays.asList("Chest"), 5, 30, 15);
        
        Exercise exercise = new Exercise("1", "Pushup", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                "Desc", 2, Collections.singletonList("img"));
        
        // Pushup uses bodyweight, but we selected dumbbells in setUp mapper stub.
        // matchesExercise should return false because of equipment mismatch.
        assertFalse(constraints.matchesExercise(exercise));
    }
}
