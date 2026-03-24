package com.example.exergen.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.IntervalTimer;
import com.example.exergen.business.service.TimerObserver;
import com.example.exergen.business.service.TimerPhase;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.UUID;

public class LiveWorkoutViewModel extends ViewModel implements TimerObserver {

    private final MutableLiveData<Integer> timeLeft = new MutableLiveData<>();
    private final MutableLiveData<TimerPhase> phase = new MutableLiveData<>();
    private final MutableLiveData<Exercise> currentExercise = new MutableLiveData<>();
    private final MutableLiveData<Exercise> nextExercise = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);

    private IntervalTimer intervalTimer;
    private Workout workout;
    private int workDuration;
    private int restDuration;
    private int configuredSets;
    
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;

    public void init(Workout workout, int workSeconds, int restSeconds) {
        init(workout, workSeconds, restSeconds, workout.getSets());
    }

    public void init(Workout workout, int workSeconds, int restSeconds, int selectedSets) {
        init(workout, workSeconds, restSeconds, selectedSets,
                AppBootstrap.get().exerciseUseCase,
                AppBootstrap.get().sessionHistoryUseCase);
    }

    public void init(Workout workout, int workSeconds, int restSeconds, 
                     ExerciseUseCase exerciseUseCase,
                     SessionHistoryUseCase sessionHistoryUseCase) {
        init(workout, workSeconds, restSeconds, workout.getSets(), exerciseUseCase, sessionHistoryUseCase);
    }

    public void init(Workout workout, int workSeconds, int restSeconds,
                     int selectedSets,
                     ExerciseUseCase exerciseUseCase,
                     SessionHistoryUseCase sessionHistoryUseCase) {
        if (this.workout != null) return; // Already initialized

        this.workout = workout;
        this.workDuration = workSeconds;
        this.restDuration = restSeconds;
        this.configuredSets = selectedSets;
        this.exerciseUseCase = exerciseUseCase;
        this.sessionHistoryUseCase = sessionHistoryUseCase;

        int totalSets = selectedSets * workout.getSteps().size();
        this.intervalTimer = new IntervalTimer(workSeconds, restSeconds, totalSets, this);
        
        timeLeft.setValue(workSeconds);
        phase.setValue(TimerPhase.WORK);
        updateExercises();
    }

    public LiveData<Integer> getTimeLeft() { return timeLeft; }
    public LiveData<TimerPhase> getPhase() { return phase; }
    public LiveData<Exercise> getCurrentExercise() { return currentExercise; }
    public LiveData<Exercise> getNextExercise() { return nextExercise; }
    public LiveData<Boolean> getIsRunning() { return isRunning; }
    public LiveData<Boolean> getIsFinished() { return isFinished; }

    public void start() {
        if (intervalTimer != null && !intervalTimer.isRunning()) {
            intervalTimer.start();
            isRunning.setValue(true);
        }
    }

    public void pause() {
        if (intervalTimer != null && intervalTimer.isRunning()) {
            intervalTimer.pause();
            isRunning.setValue(false);
        }
    }

    public void stop() {
        if (intervalTimer != null) {
            intervalTimer.cancel();
            isRunning.setValue(false);
        }
    }

    @Override
    public void onTick(long secondsRemaining) {
        timeLeft.postValue((int) secondsRemaining);
    }

    @Override
    public void onPhaseChange(TimerPhase newPhase) {
        phase.postValue(newPhase);
        updateExercises();
    }

    @Override
    public void onFinish() {
        isRunning.postValue(false);
        isFinished.postValue(true);
        saveSession();
    }

    private void updateExercises() {
        if (workout == null || intervalTimer == null || exerciseUseCase == null) return;

        int currentSetIndex = intervalTimer.getCurrentSet() - 1;
        int exerciseCount = workout.getSteps().size();
        
        // Current Exercise
        WorkoutStep currentStep = workout.getSteps().get(currentSetIndex % exerciseCount);
        String currentExId = currentStep.getExerciseId();
        Exercise currentEx = exerciseUseCase.getExerciseById(currentExId);
        currentExercise.postValue(currentEx);

        // Next Exercise
        int nextSetIndex = currentSetIndex + 1;
        if (nextSetIndex < intervalTimer.getTotalSets()) {
            WorkoutStep nextStep = workout.getSteps().get(nextSetIndex % exerciseCount);
            String nextExId = nextStep.getExerciseId();
            Exercise nextEx = exerciseUseCase.getExerciseById(nextExId);
            nextExercise.postValue(nextEx);
        } else {
            nextExercise.postValue(null);
        }
    }

    private void saveSession() {
        if (workout == null || sessionHistoryUseCase == null) return;

        int totalDuration = intervalTimer.getTotalSets() * (workDuration + restDuration);
        SessionRecord record = new SessionRecord(
                UUID.randomUUID().toString(),
                workout.getId(),
                workout.getName(),
                System.currentTimeMillis(),
                totalDuration,
                workout.getSteps().size(),
                configuredSets,
                configuredSets
        );
        
        sessionHistoryUseCase.saveCompletedSession(record);
    }
}
