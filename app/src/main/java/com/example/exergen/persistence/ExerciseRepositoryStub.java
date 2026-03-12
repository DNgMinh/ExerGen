package com.example.exergen.persistence;

import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseRepositoryStub implements IExerciseRepository {
    private final List<Exercise> exercises = new ArrayList<>();

    public ExerciseRepositoryStub() {
        exercises.add(new Exercise(
                "ex-1",
                "Pushup",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Keep a straight line from head to heels and lower with control.",
                3,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-2",
                "Squat",
                Arrays.asList(MuscleGroup.LEGS, MuscleGroup.GLUTES),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Sit back with your hips and keep your chest upright.",
                3,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-3",
                "Plank",
                Arrays.asList(MuscleGroup.CORE),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Maintain a straight line from shoulders to heels.",
                2,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-4",
                "Dumbbell Row",
                Arrays.asList(MuscleGroup.BACK),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Pull the dumbbell toward your hip with a flat back.",
                3,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-5",
                "Bicep Curl",
                Arrays.asList(MuscleGroup.ARMS),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Keep elbows tucked and curl the weight with control.",
                2,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-6",
                "Lunge",
                Arrays.asList(MuscleGroup.LEGS, MuscleGroup.GLUTES),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Step forward and lower until both knees are bent.",
                3,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-7",
                "Bench Press",
                Arrays.asList(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Lower the bar to your mid-chest and press upward.",
                4,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-8",
                "Shoulder Press",
                Arrays.asList(MuscleGroup.SHOULDERS),
                Arrays.asList(EquipmentType.DUMBBELLS),
                "Press the weights overhead without arching your back.",
                3,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-9",
                "Jumping Jacks",
                Arrays.asList(MuscleGroup.FULL_BODY),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Jump feet out while raising arms overhead.",
                2,
                "placeholder.png"));
        exercises.add(new Exercise(
                "ex-10",
                "Mountain Climbers",
                Arrays.asList(MuscleGroup.CORE, MuscleGroup.LEGS),
                Arrays.asList(EquipmentType.BODYWEIGHT),
                "Drive knees toward your chest in a plank position.",
                3,
                "placeholder.png"));
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

    @Override
    public List<Exercise> filterByEquipment(String equipment) {
        if (equipment == null || equipment.trim().isEmpty()) {
            throw new InvalidFilterException("Equipment filter must be non-empty.");
        }

        String normalizedEquipment = equipment.trim();
        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : exercises) {
            for (String currentEquipment : exercise.getEquipment()) {
                if (currentEquipment.equalsIgnoreCase(normalizedEquipment)) {
                    result.add(exercise);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<Exercise> filterByMuscleGroup(String muscle) {
        if (muscle == null || muscle.trim().isEmpty()) {
            throw new InvalidFilterException("Muscle group filter must be non-empty.");
        }

        String normalizedMuscle = muscle.trim();
        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : exercises) {
            for (String currentMuscle : exercise.getMuscleGroups()) {
                if (currentMuscle.equalsIgnoreCase(normalizedMuscle)) {
                    result.add(exercise);
                    break;
                }
            }
        }
        return result;
    }

    public void addExercise(Exercise exercise) {
        insertExercise(exercise);
    }
}
