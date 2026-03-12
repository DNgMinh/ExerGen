package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class WorkoutPreviewMapperTest {
    private WorkoutPreviewMapper mapper;
    private ExerciseService exerciseService;

    @Before
    public void setUp() {
        mapper = new WorkoutPreviewMapper();
        exerciseService = new ExerciseService(new FakeExerciseRepository(List.of(
                new Exercise("ex-1", "Pushup", List.of("Chest"), List.of("Bodyweight"), "", 2, List.of("img")),
                new Exercise("ex-2", "Squat", List.of("Legs"), List.of("Bodyweight"), "", 2, List.of("img")))));
    }

    @Test
    public void mapBuildsPreviewItemsWithExerciseNames() {
        Workout workout = new Workout(
                "w1",
                "Generated Workout",
                1,
                List.of("ex-1", "ex-2"),
                List.of(45, 45),
                List.of(15, 15));

        WorkoutPreviewData preview = mapper.map(workout, exerciseService);

        assertNotNull(preview);
        assertEquals(2, preview.getExerciseCount());
        assertEquals("Pushup", preview.getItems().get(0).getExerciseName());
        assertEquals("Squat", preview.getItems().get(1).getExerciseName());
        assertEquals(1, preview.getItems().get(0).getSequence());
        assertEquals(2, preview.getItems().get(1).getSequence());
    }

    @Test
    public void mapFallsBackToExerciseIdWhenExerciseMissing() {
        Workout workout = new Workout(
                "w2",
                "Generated Workout",
                1,
                List.of("unknown-id"),
                List.of(30),
                List.of(10));

        WorkoutPreviewData preview = mapper.map(workout, exerciseService);

        assertEquals(1, preview.getExerciseCount());
        assertEquals("unknown-id", preview.getItems().get(0).getExerciseName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapRejectsNullWorkout() {
        mapper.map(null, exerciseService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapRejectsNullExerciseService() {
        Workout workout = new Workout(
                "w3",
                "Generated Workout",
                1,
                List.of("ex-1"),
                List.of(30),
                List.of(10));

        mapper.map(workout, null);
    }

    private static class FakeExerciseRepository implements IExerciseRepository {
        private final List<Exercise> exercises;

        private FakeExerciseRepository(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public List<Exercise> getAllExercises() {
            return exercises;
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
        public Exercise getExerciseById(String id) {
            for (Exercise exercise : exercises) {
                if (exercise.getId().equals(id)) {
                    return exercise;
                }
            }
            return null;
        }

        @Override
        public void deleteExercise(String id) {
        }

        @Override
        public void seedData() {
        }
    }
}
