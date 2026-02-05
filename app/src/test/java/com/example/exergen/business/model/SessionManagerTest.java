package com.example.exergen.business.model;

import com.example.exergen.persistence.repository.ExerciseRepository;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class SessionManagerTest {

    // Fake repository for testing
    private static class FakeExerciseRepository implements ExerciseRepository {
        private final HashMap<String, Exercise> store = new HashMap<>();

        @Override
        public Exercise getById(String id) {
            return store.get(id);
        }

        @Override
        public List<Exercise> getAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public void save(Exercise e) {
            store.put(e.getId(), e);
        }
    }

    @Test
    public void testNextMovesToNextExercise() {
        // Arrange fake repo
        FakeExerciseRepository repo = new FakeExerciseRepository();
        Exercise e1 = new Exercise("e1", "First", List.of(), List.of(), "", 0, 1);
        Exercise e2 = new Exercise("e2", "Second", List.of(), List.of(), "", 0, 1);

        repo.save(e1);
        repo.save(e2);

        // Workout stores only IDs
        Workout w = new Workout(
                "w1",
                "Test Workout",
                1,
                List.of("e1", "e2"),
                List.of(0, 0),
                List.of(0, 0));

        SessionManager manager = new SessionManager(w, repo);

        // Act
        manager.next(); // move to 2nd exercise

        // Assert
        assertEquals("Second", manager.getCurrentExercise().getName());
    }
}
