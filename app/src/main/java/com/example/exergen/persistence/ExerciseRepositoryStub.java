package com.example.exergen.persistence;

import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRepositoryStub implements IExerciseRepository {
    private final List<Exercise> exercises = new ArrayList<>();

    public ExerciseRepositoryStub() {
        exercises.add(new Exercise(
                "ex-1",
                "Pushup",
                List.of("Chest", "Triceps"),
                List.of("Bodyweight"),
                "Keep a straight line from head to heels and lower with control.",
                3,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-2",
                "Squat",
                List.of("Legs", "Glutes"),
                List.of("Bodyweight"),
                "Sit back with your hips and keep your chest upright.",
                3,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-3",
                "Plank",
                List.of("Core"),
                List.of("Bodyweight"),
                "Maintain a straight line from shoulders to heels.",
                2,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-4",
                "Dumbbell Row",
                List.of("Back"),
                List.of("Dumbbells"),
                "Pull the dumbbell toward your hip with a flat back.",
                3,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-5",
                "Bicep Curl",
                List.of("Arms"),
                List.of("Dumbbells"),
                "Keep elbows tucked and curl the weight with control.",
                2,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-6",
                "Lunge",
                List.of("Legs", "Glutes"),
                List.of("Bodyweight"),
                "Step forward and lower until both knees are bent.",
                3,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-7",
                "Bench Press",
                List.of("Chest", "Triceps"),
                List.of("Barbell"),
                "Lower the bar to your mid-chest and press upward.",
                4,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-8",
                "Shoulder Press",
                List.of("Shoulders"),
                List.of("Dumbbells"),
                "Press the weights overhead without arching your back.",
                3,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-9",
                "Jumping Jacks",
                List.of("Full Body"),
                List.of("Bodyweight"),
                "Jump feet out while raising arms overhead.",
                2,
                List.of("placeholder.png")));
        exercises.add(new Exercise(
                "ex-10",
                "Mountain Climbers",
                List.of("Core", "Legs"),
                List.of("Bodyweight"),
                "Drive knees toward your chest in a plank position.",
                3,
                List.of("placeholder.png")));
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
        exercises.removeIf(exercise -> exercise.getId().equals(id));
    }

    @Override
    public void seedData() {
        // The stub automatically seeds data in its constructor,
        // so this method can safely remain empty.
    }

    @Override
    public List<Exercise> getAllExercises() {
        return List.copyOf(exercises);
    }

    @Override
    public void insertExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise required");
        }

        for (Exercise existing : exercises) {
            if (existing.getId().equals(exercise.getId())) {
                throw new DuplicateExerciseException(exercise.getId());
            }
        }

        exercises.add(exercise);
    }

    public List<Exercise> filterByEquipment(String equipment) {
        if (equipment == null || equipment.isEmpty()) {
            throw new InvalidFilterException("Equipment filter must be non-empty.");
        }

        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : exercises) {
            if (exercise.getEquipment().contains(equipment)) {
                result.add(exercise);
            }
        }
        return result;
    }

    public List<Exercise> filterByMuscleGroup(String muscle) {
        if (muscle == null || muscle.isEmpty()) {
            throw new InvalidFilterException("Muscle group filter must be non-empty.");
        }

        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : exercises) {
            if (exercise.getMuscleGroups().contains(muscle)) {
                result.add(exercise);
            }
        }
        return result;
    }

    public void addExercise(Exercise exercise) {
        insertExercise(exercise);
    }
}
