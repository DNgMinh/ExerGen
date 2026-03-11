package com.example.exergen.business.usecase;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.repository.IWorkoutRepository;
import com.example.exergen.model.Workout;
import com.example.exergen.model.Exercise;

import java.util.Arrays;
import java.util.List;

public class WorkoutUseCaseTest {

    private WorkoutUseCase workoutUseCase;

    @Mock
    private IWorkoutRepository mockWorkoutRepository;

    @Mock
    private ExerciseService mockExerciseService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        workoutUseCase = new WorkoutUseCase(mockWorkoutRepository, mockExerciseService);
    }

    @Test
    public void testGetWorkoutById_Valid() {
        Workout expectedWorkout = new Workout("w1", "Test Workout", 1, List.of("ex1"), List.of(30), List.of(10));
        when(mockWorkoutRepository.getWorkoutById("w1")).thenReturn(expectedWorkout);

        Workout result = workoutUseCase.getWorkoutById("w1");

        assertNotNull(result);
        assertEquals("Test Workout", result.getName());
        verify(mockWorkoutRepository).getWorkoutById("w1");
    }

    @Test
    public void testGetWorkoutById_Invalid() {
        assertNull(workoutUseCase.getWorkoutById(null));
        assertNull(workoutUseCase.getWorkoutById(""));

        when(mockWorkoutRepository.getWorkoutById("missing")).thenReturn(null);
        assertNull(workoutUseCase.getWorkoutById("missing"));
    }

    @Test
    public void testGetAllWorkouts_DelegatesToRepository() {
        List<Workout> workouts = Arrays.asList(
                new Workout("w1", "W1", 1, List.of("e1"), List.of(1), List.of(1)),
                new Workout("w2", "W2", 1, List.of("e2"), List.of(1), List.of(1))
        );
        when(mockWorkoutRepository.getAllWorkouts()).thenReturn(workouts);

        List<Workout> result = workoutUseCase.getAllWorkouts();

        assertEquals(2, result.size());
        verify(mockWorkoutRepository).getAllWorkouts();
    }

    @Test
    public void testSaveWorkout_ValidatesAndSaves() {
        Workout workout = new Workout("new", "New", 1, List.of("e1"), List.of(30), List.of(10));

        workoutUseCase.saveWorkout(workout);

        verify(mockWorkoutRepository).saveWorkout(workout);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveWorkout_ThrowsOnNull() {
        workoutUseCase.saveWorkout(null);
    }

    @Test
    public void testDeleteWorkout_ValidatesAndDeletes() {
        workoutUseCase.deleteWorkout("w1");
        verify(mockWorkoutRepository).deleteWorkout("w1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWorkout_RejectsBlankId() {
        workoutUseCase.deleteWorkout(" ");
    }

    @Test
    public void testGetExercisesForWorkout_ResolvesCorrectly() {
        Workout workout = new Workout("w1", "Workout", 1, Arrays.asList("ex1", "ex2"), Arrays.asList(30, 30), Arrays.asList(10, 10));
        Exercise e1 = new Exercise("ex1", "E1", List.of("M1"), List.of("Eq1"), "D1", 1, List.of("P1"));
        Exercise e2 = new Exercise("ex2", "E2", List.of("M2"), List.of("Eq2"), "D2", 2, List.of("P2"));

        when(mockExerciseService.getExerciseById("ex1")).thenReturn(e1);
        when(mockExerciseService.getExerciseById("ex2")).thenReturn(e2);

        List<Exercise> results = workoutUseCase.getExercisesForWorkout(workout);

        assertEquals(2, results.size());
        assertEquals("E1", results.get(0).getName());
        assertEquals("E2", results.get(1).getName());
    }

    @Test
    public void testGetExercisesForWorkout_HandlesMissingExercises() {
        Workout workout = new Workout("w1", "Workout", 1, List.of("ghost"), List.of(30), List.of(10));
        when(mockExerciseService.getExerciseById("ghost")).thenReturn(null);

        List<Exercise> results = workoutUseCase.getExercisesForWorkout(workout);

        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetExercisesForWorkout_NullWorkoutReturnsEmpty() {
        List<Exercise> results = workoutUseCase.getExercisesForWorkout(null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}