package com.example.exergen.business.service;

import com.example.exergen.business.service.IntervalTimer;
import com.example.exergen.business.service.TimerObserver;
import com.example.exergen.business.service.TimerPhase;
import com.example.exergen.model.Exercise;

import java.util.List;

public class LiveWorkoutManager implements TimerObserver{
    private final List<Exercise> routine;
    private final IntervalTimer intervalTimer;
    private final WorkoutUpdateListener uiListener;

    public LiveWorkoutManager(List<Exercise> routine, int workDurationSeconds, int restDurationSeconds, WorkoutUpdateListener uiListener) {
        if (routine == null || routine.isEmpty()) {
            throw new IllegalArgumentException("Cannot start an empty routine.");
        }

        this.routine = routine;
        this.uiListener = uiListener;

        this.intervalTimer = new IntervalTimer(workDurationSeconds, restDurationSeconds, routine.size(), this);
    }


    public void start() {
        intervalTimer.start();
    }

    public void pause() {
        intervalTimer.pause();
    }

    public void resume() {
        intervalTimer.start();
    }

    public void skip() {
        intervalTimer.skipPhase();
    }

    public void stop() {
        intervalTimer.cancel();
    }

    @Override
    public void onTick(long secondsRemaining) {
        uiListener.onTick((int) secondsRemaining);
    }

    @Override
    public void onPhaseChange(TimerPhase phase) {

        int listIndex = intervalTimer.getCurrentSet() - 1;

        Exercise currentExercise = null;
        Exercise nextExercise = null;

        if (listIndex >= 0 && listIndex < routine.size()) {
            currentExercise = routine.get(listIndex);
        }

        if (listIndex + 1 < routine.size()) {
            nextExercise = routine.get(listIndex + 1);
        }

        uiListener.onStateChanged(currentExercise, nextExercise, phase.name());

        uiListener.onTick(intervalTimer.getRemainingSeconds());
    }

    @Override
    public void onFinish() {
        uiListener.onWorkoutFinished();
    }
}
