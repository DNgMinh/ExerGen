package com.example.exergen.business.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.exergen.model.Exercise;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiveWorkoutManagerTest {
    private WorkoutUpdateListener mockListener;
    private Exercise exercise1;
    private Exercise exercise2;

    @Before
    public void setUp() {
        mockListener = mock(WorkoutUpdateListener.class);

        exercise1 = mock(Exercise.class);
        exercise2 = mock(Exercise.class);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullRoutine_ThrowsException() {
        new LiveWorkoutManager(null, 30, 10, mockListener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_EmptyRoutine_ThrowsException() {
        new LiveWorkoutManager(Collections.emptyList(), 30, 10, mockListener);
    }


    @Test
    public void testOnTick_DelegatesDirectlyToListener() {
        List<Exercise> routine = List.of(exercise1);
        LiveWorkoutManager manager = new LiveWorkoutManager(routine, 30, 10, mockListener);

        manager.onTick(25L);

        verify(mockListener).onTick(25);
    }

    @Test
    public void testOnFinish_DelegatesDirectlyToListener() {
        List<Exercise> routine = List.of(exercise1);
        LiveWorkoutManager manager = new LiveWorkoutManager(routine, 30, 10, mockListener);

        manager.onFinish();

        verify(mockListener).onWorkoutFinished();
    }


    @Test
    public void testStateTransitions_CorrectlyMapsSetsToExercises() {
        List<Exercise> routine = List.of(exercise1, exercise2);

        LiveWorkoutManager manager = new LiveWorkoutManager(routine, 30, 10, mockListener);

        manager.start();
        verify(mockListener).onStateChanged(eq(exercise1), eq(exercise2), eq("WORK"));

        manager.skip();
        verify(mockListener).onStateChanged(eq(exercise1), eq(exercise2), eq("REST"));

        manager.skip();
        verify(mockListener).onStateChanged(eq(exercise2), isNull(), eq("WORK"));

        manager.stop();
    }

    @Test
    public void testSingleExerciseRoutine_NextExerciseIsNullFromStart() {
        List<Exercise> routine = List.of(exercise1);
        LiveWorkoutManager manager = new LiveWorkoutManager(routine, 30, 10, mockListener);

        manager.start();

        verify(mockListener).onStateChanged(eq(exercise1), isNull(), eq("WORK"));

        manager.stop();
    }
}
