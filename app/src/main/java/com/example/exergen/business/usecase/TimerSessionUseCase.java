package com.example.exergen.business.usecase;

import com.example.exergen.business.service.IntervalTimer;
import com.example.exergen.business.service.TimerObserver;
import com.example.exergen.business.service.TimerPhase;

import java.util.List;

public class TimerSessionUseCase {
    private IntervalTimer intervalTimer;
    private TimerSessionObserver observer;

    public void initialize(List<Integer> workDurations, List<Integer> restDurations, int totalSets, TimerSessionObserver observer) {
        this.observer = observer;
        if (intervalTimer == null) {
            intervalTimer = new IntervalTimer(workDurations, restDurations, totalSets, new ForwardingObserver());
        }
    }

    public void startOrResume(List<Integer> workDurations, List<Integer> restDurations, int totalSets, TimerSessionObserver observer) {
        initialize(workDurations, restDurations, totalSets, observer);
        if (!intervalTimer.isRunning()) {
            intervalTimer.start();
        }
    }

    public void startOrResume(int workSeconds, int restSeconds, int totalSets, TimerSessionObserver observer) {
        startOrResume(List.of(workSeconds), List.of(restSeconds), totalSets, observer);
    }

    public void pause() {
        if (intervalTimer != null && intervalTimer.isRunning()) {
            intervalTimer.pause();
        }
    }

    public void stop() {
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
    }

    public boolean hasActiveSession() {
        return intervalTimer != null;
    }

    public boolean isRunning() {
        return intervalTimer != null && intervalTimer.isRunning();
    }

    public List<Integer> getWorkDurations() {
        return intervalTimer != null ? intervalTimer.getWorkDurations() : null;
    }

    public List<Integer> getRestDurations() {
        return intervalTimer != null ? intervalTimer.getRestDurations() : null;
    }

    public int getWorkDurationSeconds() {
        List<Integer> durations = getWorkDurations();
        return (durations != null && !durations.isEmpty()) ? durations.get(0) : 0;
    }

    public int getRestDurationSeconds() {
        List<Integer> durations = getRestDurations();
        return (durations != null && !durations.isEmpty()) ? durations.get(0) : 0;
    }

    public int getTotalSets() {
        return intervalTimer != null ? intervalTimer.getTotalSets() : 0;
    }

    public int getCurrentSet() {
        return intervalTimer != null ? intervalTimer.getCurrentSet() : 1;
    }

    public TimerMode getCurrentMode() {
        return intervalTimer != null && intervalTimer.getCurrentPhase() == TimerPhase.REST
                ? TimerMode.REST
                : TimerMode.WORK;
    }

    public int getRemainingSeconds() {
        return intervalTimer != null ? intervalTimer.getRemainingSeconds() : 0;
    }

    public void restoreState(
            List<Integer> workDurations,
            List<Integer> restDurations,
            int totalSets,
            int currentSet,
            TimerMode mode,
            int remainingSeconds,
            boolean shouldBeRunning,
            TimerSessionObserver observer) {
        this.observer = observer;
        intervalTimer = new IntervalTimer(workDurations, restDurations, totalSets, new ForwardingObserver());
        intervalTimer.restoreState(
                currentSet,
                mode == TimerMode.REST ? TimerPhase.REST : TimerPhase.WORK,
                remainingSeconds);
        if (shouldBeRunning) {
            intervalTimer.start();
        }
    }

    public void restoreState(
            int workSeconds,
            int restSeconds,
            int totalSets,
            int currentSet,
            TimerMode mode,
            int remainingSeconds,
            boolean shouldBeRunning,
            TimerSessionObserver observer) {
        restoreState(List.of(workSeconds), List.of(restSeconds), totalSets, currentSet, mode, remainingSeconds, shouldBeRunning, observer);
    }

    private class ForwardingObserver implements TimerObserver {
        @Override
        public void onTick(long secondsRemaining) {
            if (observer != null) {
                observer.onTick(secondsRemaining);
            }
        }

        @Override
        public void onPhaseChange(TimerPhase phase) {
            if (observer != null) {
                observer.onModeChange(phase == TimerPhase.REST ? TimerMode.REST : TimerMode.WORK);
            }
        }

        @Override
        public void onFinish() {
            if (observer != null) {
                observer.onFinish();
            }
        }
    }
}
