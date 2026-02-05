package com.example.exergen.business;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionManagerTest {
    @Test
    public void testNextStepMovesToNextExercise() {
        // Arrange
        List<Exercise> list = new ArrayList<>();
        list.add(new Exercise(
                "ex-1",
                "First",
                "Test instructions.",
                Collections.emptyList(),
                Collections.emptyList(),
                10,
                1));
        list.add(new Exercise(
                "ex-2",
                "Second",
                "Test instructions.",
                Collections.emptyList(),
                Collections.emptyList(),
                10,
                1));
        Workout w = new Workout("Test", list);
        SessionManager manager = new SessionManager(w);

        // Act
        manager.nextStep();

        // Assert
        assertEquals("Second", manager.getCurrentExercise().getName());
    }
}