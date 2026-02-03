package com.example.exergen.business;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.exergen.objects.Exercise;
import com.example.exergen.objects.Workout;
import java.util.ArrayList;
import java.util.List;

public class SessionManagerTest {
    @Test
    public void testNextStepMovesToNextExercise() {
        // Arrange
        List<Exercise> list = new ArrayList<>();
        list.add(new Exercise("First", 10));
        list.add(new Exercise("Second", 10));
        Workout w = new Workout("Test", list);
        SessionManager manager = new SessionManager(w);

        // Act
        manager.nextStep();

        // Assert
        assertEquals("Second", manager.getCurrentExercise().getName());
    }
}