package com.example.exergen.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.TimerMode;
import com.example.exergen.business.usecase.TimerSessionObserver;
import com.example.exergen.business.usecase.TimerSessionUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.List;
import java.util.UUID;

public class LiveWorkoutViewModel extends ViewModel implements TimerSessionObserver {

    private final MutableLiveData<Integer> timeLeft = new MutableLiveData<>();
    private final MutableLiveData<TimerMode> phase = new MutableLiveData<>();
    private final MutableLiveData<Exercise> currentExercise = new MutableLiveData<>();
    private final MutableLiveData<Exercise> nextExercise = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);
    private final MutableLiveData<LiveWorkoutUiState> uiState = new MutableLiveData<>(LiveWorkoutUiState.setup());

    private final TimerSessionUseCase timerSessionUseCase = new TimerSessionUseCase();
    private Workout workout;
    private int configuredSets;
    
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;

    public void init(Workout workout, int workSeconds, int restSeconds, 
                     ExerciseUseCase exerciseUseCase,
                     SessionHistoryUseCase sessionHistoryUseCase,
                     CaloriesEstimationUseCase caloriesEstimationUseCase) {
        init(
                workout,
                workSeconds,
                restSeconds,
                workout.getSets(),
                exerciseUseCase,
                sessionHistoryUseCase,
                caloriesEstimationUseCase);
    }

    public void init(Workout workout, int workSeconds, int restSeconds,
                     int selectedSets,
                     ExerciseUseCase exerciseUseCase,
                     SessionHistoryUseCase sessionHistoryUseCase,
                     CaloriesEstimationUseCase caloriesEstimationUseCase) {
        if (this.workout != null) return; // Already initialized

        this.workout = workout;
        this.configuredSets = selectedSets;
        this.exerciseUseCase = exerciseUseCase;
        this.sessionHistoryUseCase = sessionHistoryUseCase;
        this.caloriesEstimationUseCase = caloriesEstimationUseCase;

        int totalSets = selectedSets * workout.getSteps().size();
        timerSessionUseCase.initialize(workout.getWorkSeconds(), workout.getRestSeconds(), totalSets, this);
        
        // Show initial state immediately
        timeLeft.setValue(workout.getWorkSeconds().get(0));
        phase.setValue(TimerMode.WORK);
        updateExercises();
    }

    public LiveData<Integer> getTimeLeft() { return timeLeft; }
    public LiveData<TimerMode> getPhase() { return phase; }
    public LiveData<Exercise> getCurrentExercise() { return currentExercise; }
    public LiveData<Exercise> getNextExercise() { return nextExercise; }
    public LiveData<Boolean> getIsRunning() { return isRunning; }
    public LiveData<Boolean> getIsFinished() { return isFinished; }
    public LiveData<LiveWorkoutUiState> getUiState() { return uiState; }

    public int getCurrentRound() {
        if (workout == null || !timerSessionUseCase.hasActiveSession()) return 1;
        int currentSetIndex = timerSessionUseCase.getCurrentSet() - 1;
        int exerciseCount = workout.getSteps().size();
        return (currentSetIndex / exerciseCount) + 1;
    }

    public void startWorkout(
            Workout workout,
            int workSeconds,
            int restSeconds,
            int selectedSets,
            ExerciseUseCase exerciseUseCase,
            SessionHistoryUseCase sessionHistoryUseCase,
            CaloriesEstimationUseCase caloriesEstimationUseCase) {
        init(
                workout,
                workSeconds,
                restSeconds,
                selectedSets,
                exerciseUseCase,
                sessionHistoryUseCase,
                caloriesEstimationUseCase);
        start();
    }

    public void start() {
        if (timerSessionUseCase.hasActiveSession() && !timerSessionUseCase.isRunning()) {
            timerSessionUseCase.startOrResume(
                    timerSessionUseCase.getWorkDurations(),
                    timerSessionUseCase.getRestDurations(),
                    timerSessionUseCase.getTotalSets(),
                    this);
            isRunning.setValue(true);
            uiState.setValue(LiveWorkoutUiState.activeRunning());
        }
    }

    public void pause() {
        if (timerSessionUseCase.isRunning()) {
            timerSessionUseCase.pause();
            isRunning.setValue(false);
            uiState.setValue(LiveWorkoutUiState.activePaused());
        }
    }

    public void stop() {
        if (timerSessionUseCase.hasActiveSession()) {
            timerSessionUseCase.stop();
            isRunning.setValue(false);
            uiState.setValue(LiveWorkoutUiState.setup());
        }
    }

    @Override
    public void onTick(long secondsRemaining) {
        timeLeft.postValue((int) secondsRemaining);
    }

    @Override
    public void onModeChange(TimerMode newPhase) {
        phase.postValue(newPhase);
        updateExercises();
    }

    @Override
    public void onFinish() {
        isRunning.postValue(false);
        isFinished.postValue(true);
        uiState.postValue(LiveWorkoutUiState.finished());
        saveSession();
    }

    private void updateExercises() {
        if (workout == null || !timerSessionUseCase.hasActiveSession() || exerciseUseCase == null) return;

        int currentSetIndex = timerSessionUseCase.getCurrentSet() - 1;
        int exerciseCount = workout.getSteps().size();
        
        // Current Exercise
        WorkoutStep currentStep = workout.getSteps().get(currentSetIndex % exerciseCount);
        String currentExId = currentStep.getExerciseId();
        Exercise currentEx = exerciseUseCase.getExerciseById(currentExId);
        currentExercise.postValue(currentEx);

        // Next Exercise
        int nextSetIndex = currentSetIndex + 1;
        if (nextSetIndex < timerSessionUseCase.getTotalSets()) {
            WorkoutStep nextStep = workout.getSteps().get(nextSetIndex % exerciseCount);
            String nextExId = nextStep.getExerciseId();
            Exercise nextEx = exerciseUseCase.getExerciseById(nextExId);
            nextExercise.postValue(nextEx);
        } else {
            nextExercise.postValue(null);
        }
    }

    private void saveSession() {
        if (workout == null || sessionHistoryUseCase == null || !timerSessionUseCase.hasActiveSession()) return;

        int totalDuration = 0;
        for (WorkoutStep step : workout.getSteps()) {
            totalDuration += configuredSets * (step.getWorkSeconds() + step.getRestSeconds());
        }

        int estimatedCalories = SessionRecord.UNKNOWN_ESTIMATED_CALORIES;
        if (caloriesEstimationUseCase != null) {
            estimatedCalories = calculateWorkoutCaloriesByExerciseStep();
        }
        SessionRecord record = new SessionRecord(
                UUID.randomUUID().toString(),
                workout.getId(),
                workout.getName(),
                System.currentTimeMillis(),
                totalDuration,
                workout.getSteps().size(),
                configuredSets,
                configuredSets,
                estimatedCalories
        );
        
        sessionHistoryUseCase.saveCompletedSession(record);
    }

    private int calculateWorkoutCaloriesByExerciseStep() {
        if (workout == null || workout.getSteps() == null || workout.getSteps().isEmpty() || exerciseUseCase == null) {
            return caloriesEstimationUseCase != null ? caloriesEstimationUseCase.estimateCaloriesWithDefaultIntensity(0) : 0;
        }

        int totalEstimatedCalories = 0;
        for (WorkoutStep step : workout.getSteps()) {
            Exercise exercise = exerciseUseCase.getExerciseById(step.getExerciseId());
            int intensity = resolveIntensity(exercise);
            int stepDurationSecondsAcrossSets = configuredSets * (step.getWorkSeconds() + step.getRestSeconds());
            totalEstimatedCalories += caloriesEstimationUseCase.estimateCalories(stepDurationSecondsAcrossSets, intensity);
        }
        return totalEstimatedCalories;
    }

    private int resolveIntensity(Exercise exercise) {
        if (exercise == null) {
            return 3;
        }
        int intensity = exercise.getIntensity();
        if (intensity < 1 || intensity > 5) {
            return 3;
        }
        return intensity;
    }
}
