package com.example.exergen.business.model;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.service.SessionManager;
import com.example.exergen.model.Workout;
import com.example.exergen.business.repository.IExerciseRepository;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class SessionManagerTest {

    private static class FakeExerciseRepository implements IExerciseRepository {
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
            return List.of();
        }

        @Override
        public List<Exercise> filterByMuscleGroup(String muscleGroup) {
            return List.of();
        }

        @Override
        public void insertExercise(Exercise exercise) {
        }

        @Override
        public void deleteExercise(String id) {
        }

        @Override
        public void seedData() {
        }
    }

    @Test
    public void testNextMovesToNextExercise() {
        Exercise e1 = new Exercise("e1", "First", List.of(), List.of(), "", 1, "placeholder");
        Exercise e2 = new Exercise("e2", "Second", List.of(), List.of(), "", 1, "placeholder");
        FakeExerciseRepository repo = new FakeExerciseRepository(List.of(e1, e2));

        Workout w = new Workout(
                "w1",
                "Test Workout",
                1,
                List.of("e1", "e2"),
                List.of(0, 0),
                List.of(0, 0));

        SessionManager manager = new SessionManager(w, repo);

        manager.next();

        assertEquals("Second", manager.getCurrentExercise().getName());
    }
}