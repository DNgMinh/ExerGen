package com.example.exergen.business.model;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.service.SessionManager;
import com.example.exergen.model.Workout;
import com.example.exergen.persistence.repository.ExerciseRepository;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class SessionManagerTest {

    // Fake repository for testing
    private static class FakeExerciseRepository implements ExerciseRepository {
        private final HashMap<String, Exercise> store = new HashMap<>();
        private final List<Exercise> allExercises;

        private FakeExerciseRepository(List<Exercise> exercises) {
            for (Exercise exercise : exercises) {
                store.put(exercise.getId(), exercise);
            }
            this.allExercises = List.copyOf(exercises);
        }

        @Override
        public Exercise getExerciseById(String id) {
            return store.get(id);
        }

        @Override
        public List<Exercise> getAllExercises() {
            return allExercises;
        }

        @Override
        public List<Exercise> filterByEquipment(String equipment) {
            if (equipment == null || equipment.isEmpty()) {
                return allExercises;
            }

            return allExercises.stream()
                    .filter(exercise -> exercise.getEquipment().contains(equipment))
                    .toList();
        }

        @Override
        public List<Exercise> filterByMuscleGroup(String muscle) {
            if (muscle == null || muscle.isEmpty()) {
                return allExercises;
            }

            return allExercises.stream()
                    .filter(exercise -> exercise.getMuscleGroups().contains(muscle))
                    .toList();
        }

        @Override
        public void addExercise(Exercise exercise) {
            // Not needed for these tests.
        }
    }

    @Test
    public void testNextMovesToNextExercise() {
        // Arrange fake repo
        Exercise e1 = new Exercise("e1", "First", List.of(), List.of(), "", 1);
        Exercise e2 = new Exercise("e2", "Second", List.of(), List.of(), "", 1);
        FakeExerciseRepository repo = new FakeExerciseRepository(List.of(e1, e2));

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
