package com.example.exergen.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.TimerMode;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.Workout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LiveWorkoutViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LiveWorkoutViewModel viewModel;
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;

    @Before
    public void setUp() {
        viewModel = new LiveWorkoutViewModel();
        exerciseUseCase = mock(ExerciseUseCase.class);
        sessionHistoryUseCase = mock(SessionHistoryUseCase.class);
        caloriesEstimationUseCase = mock(CaloriesEstimationUseCase.class);
        
        Exercise mockExercise = new Exercise(
            "e1", "Pushups", 
            List.of(MuscleGroup.CHEST), 
            List.of(EquipmentType.BODYWEIGHT), 
            "desc", 1, List.of("img.png")
        );

        // Stub for exercise lookup
        when(exerciseUseCase.getExerciseById(anyString())).thenReturn(mockExercise);
        when(caloriesEstimationUseCase.estimateCalories(anyInt(), anyInt())).thenReturn(100);
    }

    @Test
    public void testInitialization() {
        Workout workout = new Workout("w1", "Test Workout", 1, 
                Arrays.asList("e1", "e2"), Arrays.asList(30, 30), Arrays.asList(10, 10));
        
        viewModel.init(workout, 30, 10, workout.getSets(), exerciseUseCase, sessionHistoryUseCase, caloriesEstimationUseCase);
        
        assertEquals(Integer.valueOf(30), viewModel.getTimeLeft().getValue());
        assertEquals(TimerMode.WORK, viewModel.getPhase().getValue());
        assertFalse(viewModel.getIsRunning().getValue());
        assertFalse(viewModel.getIsFinished().getValue());
    }

    @Test
    public void testStartPause() {
        Workout workout = new Workout("w1", "Test Workout", 1, 
                Arrays.asList("e1"), Arrays.asList(30), Arrays.asList(10));
        
        viewModel.init(workout, 30, 10, workout.getSets(), exerciseUseCase, sessionHistoryUseCase, caloriesEstimationUseCase);
        viewModel.start();
        assertTrue(viewModel.getIsRunning().getValue());
        
        viewModel.pause();
        assertFalse(viewModel.getIsRunning().getValue());
    }

    @Test
    public void testOnFinish() {
        Workout workout = new Workout("w1", "Test Workout", 1, 
                Arrays.asList("e1"), Arrays.asList(30), Arrays.asList(10));
        
        viewModel.init(workout, 30, 10, workout.getSets(), exerciseUseCase, sessionHistoryUseCase, caloriesEstimationUseCase);
        viewModel.onFinish();
        
        assertFalse(viewModel.getIsRunning().getValue());
        assertTrue(viewModel.getIsFinished().getValue());
    }
}
