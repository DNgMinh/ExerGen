package com.example.exergen.business.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExerciseConstraintMatcherTest {

    @Test
    public void testMatches_NullExercise_ReturnsFalse() {
        assertFalse(ExerciseConstraintMatcher.matches(null, Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    public void testMatches_MatchingExercise_ReturnsTrue() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertTrue(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                Collections.singletonList(MuscleGroup.CHEST)));
    }

    @Test
    public void testMatches_EquipmentMismatch_ReturnsFalse() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BARBELL), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertFalse(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.DUMBBELLS), 
                Collections.singletonList(MuscleGroup.CHEST)));
    }

    @Test
    public void testMatches_MuscleMismatch_ReturnsFalse() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.LEGS), 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertFalse(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                Collections.singletonList(MuscleGroup.CHEST)));
    }

    @Test
    public void testMatches_EmptySelectedEquipment_AllowsAny() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BARBELL), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertTrue(ExerciseConstraintMatcher.matches(exercise, 
                Collections.emptyList(), 
                Collections.singletonList(MuscleGroup.CHEST)));
    }

    @Test
    public void testMatches_NullSelectedEquipment_AllowsAny() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BARBELL), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertTrue(ExerciseConstraintMatcher.matches(exercise, 
                null, 
                Collections.singletonList(MuscleGroup.CHEST)));
    }

    @Test
    public void testMatches_EmptyMuscleList_ReturnsFalse() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                "Instructions", 2, Collections.singletonList("img"));
        
        // Even if equipment matches, empty muscle filter should return false 
        // as per current Exercise.matches implementation requirements.
        assertFalse(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                Collections.emptyList()));
    }

    @Test
    public void testMatches_NullMuscleList_ReturnsFalse() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertFalse(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                null));
    }

    @Test
    public void testMatches_MultipleMuscles_MatchesOne() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS), 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertTrue(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.BODYWEIGHT), 
                Collections.singletonList(MuscleGroup.TRICEPS)));
    }

    @Test
    public void testMatches_MultipleEquipment_MatchesOne() {
        Exercise exercise = new Exercise("1", "Exercise", 
                Collections.singletonList(MuscleGroup.CHEST), 
                Arrays.asList(EquipmentType.BARBELL, EquipmentType.DUMBBELLS), 
                "Instructions", 2, Collections.singletonList("img"));
        
        assertTrue(ExerciseConstraintMatcher.matches(exercise, 
                Collections.singletonList(EquipmentType.DUMBBELLS),
                Collections.singletonList(MuscleGroup.CHEST)));
    }
}
