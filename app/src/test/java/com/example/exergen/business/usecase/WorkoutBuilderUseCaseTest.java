package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class WorkoutBuilderUseCaseTest {

    private WorkoutBuilderUseCase workoutBuilderUseCase;

    @Mock
    private ExerciseService mockExerciseService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        workoutBuilderUseCase = new WorkoutBuilderUseCase(mockExerciseService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateWorkoutRejectsNullConstraints() {
        workoutBuilderUseCase.generateWorkout(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateWorkoutRejectsNoMatchingExercises() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Kettlebell"),
                List.of("Shoulders"),
                10
        );

        when(mockExerciseService.filterByConstraints(anyList(), anyList())).thenReturn(List.of());

        workoutBuilderUseCase.generateWorkout(constraints);
    }

    @Test
    public void generateWorkoutBuildsRoutineForDurationAndConstraints() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                List.of("Dumbbells"),
                List.of("Chest"),
                3
        );

        Exercise e1 = new Exercise("ex-chest-db", "Dumbbell Press", List.of("Chest"), List.of("Dumbbells"), "", 3, List.of("img"));
        when(mockExerciseService.filterByConstraints(constraints.getSelectedEquipment(), constraints.getTargetMuscleGroups()))
                .thenReturn(List.of(e1));

        Workout result = workoutBuilderUseCase.generateWorkout(constraints);

        assertEquals("Generated Workout", result.getName());
        assertEquals(1, result.getRounds());
        assertEquals(3, result.getExerciseIds().size());
        for (String id : result.getExerciseIds()) {
            assertEquals("ex-chest-db", id);
        }
    }

    @Test
    public void generateWorkoutAllowsEmptyEquipmentSelection() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(), List.of("Legs"), 2);
        Exercise e1 = new Exercise("ex-legs-bw", "Air Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, List.of("img"));

        when(mockExerciseService.filterByConstraints(eq(List.of()), eq(List.of("Legs"))))
                .thenReturn(List.of(e1));

        Workout result = workoutBuilderUseCase.generateWorkout(constraints);

        assertEquals(2, result.getExerciseIds().size());
        assertTrue(result.getExerciseIds().contains("ex-legs-bw"));
    }

    @Test
    public void generateWorkoutCyclesMatchingExercisesWhenDurationRequiresMoreSlots() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of(), List.of("Chest", "Legs"), 3);

        Exercise e1 = new Exercise("e1", "E1", List.of("Chest"), List.of("BW"), "", 2, List.of("img1"));
        Exercise e2 = new Exercise("e2", "E2", List.of("Legs"), List.of("BW"), "", 2, List.of("img2"));

        when(mockExerciseService.filterByConstraints(anyList(), anyList()))
                .thenReturn(Arrays.asList(e1, e2));

        Workout result = workoutBuilderUseCase.generateWorkout(constraints);

        assertEquals(3, result.getExerciseIds().size());
        
        // Due to shuffling, we check that it cycles through the available exercises correctly
        String id0 = result.getExerciseIds().get(0);
        String id1 = result.getExerciseIds().get(1);
        String id2 = result.getExerciseIds().get(2);

        assertTrue("First exercise should be e1 or e2", id0.equals("e1") || id0.equals("e2"));
        assertTrue("Second exercise should be e1 or e2", id1.equals("e1") || id1.equals("e2"));
        assertNotEquals("Should cycle to a different exercise", id0, id1);
        assertEquals("Should cycle back to the first exercise", id0, id2);
    }

    @Test
    public void generateWorkoutAssignsUniqueIdsAcrossGenerations() {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(List.of("Dumbbells"), List.of("Chest"), 2);
        Exercise e1 = new Exercise("e1", "E1", List.of("Chest"), List.of("DB"), "", 2, List.of("img1"));

        when(mockExerciseService.filterByConstraints(anyList(), anyList()))
                .thenReturn(List.of(e1));

        Workout first = workoutBuilderUseCase.generateWorkout(constraints);
        Workout second = workoutBuilderUseCase.generateWorkout(constraints);

        assertTrue(first.getId().startsWith("generated-"));
        assertTrue(second.getId().startsWith("generated-"));
        assertNotEquals(first.getId(), second.getId());
    }
}