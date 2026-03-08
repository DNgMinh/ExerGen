package com.example.exergen.business.model;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.exception.ExerciseNotFoundException;
import com.example.exergen.business.exception.SessionCompletedException;
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
        Exercise e1 = new Exercise("e1", "First", List.of("Chest"), List.of("Bodyweight"), "", 1, "placeholder");
        Exercise e2 = new Exercise("e2", "Second", List.of("Chest"), List.of("Bodyweight"), "", 1, "placeholder");
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

    @Test(expected = SessionCompletedException.class)
    public void getCurrentExerciseThrowsWhenSessionFinished() {
        Exercise e1 = new Exercise("e1", "Only", List.of("Chest"), List.of("Bodyweight"), "", 1, "placeholder");
        FakeExerciseRepository repo = new FakeExerciseRepository(List.of(e1));
        Workout workout = new Workout("w1", "Test", 1, List.of("e1"), List.of(10), List.of(5));
        SessionManager manager = new SessionManager(workout, repo);

        manager.next();
        manager.getCurrentExercise();
    }

    @Test(expected = ExerciseNotFoundException.class)
    public void getCurrentExerciseThrowsWhenExerciseMissingFromRepository() {
        FakeExerciseRepository repo = new FakeExerciseRepository(List.of());
        Workout workout = new Workout("w1", "Missing Exercise", 1, List.of("ghost-id"), List.of(10), List.of(5));
        SessionManager manager = new SessionManager(workout, repo);

        manager.getCurrentExercise();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullWorkout() {
        Exercise e1 = new Exercise("e1", "Only", List.of("Chest"), List.of("Bodyweight"), "", 1, "placeholder");
        FakeExerciseRepository repo = new FakeExerciseRepository(List.of(e1));
        new SessionManager(null, repo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullRepository() {
        Workout workout = new Workout("w1", "Test", 1, List.of("e1"), List.of(10), List.of(5));
        new SessionManager(workout, null);
    }
}