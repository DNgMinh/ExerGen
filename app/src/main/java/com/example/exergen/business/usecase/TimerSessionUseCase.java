package com.example.exergen.business.usecase;

import com.example.exergen.business.service.IntervalTimer;
import com.example.exergen.business.service.TimerObserver;
import com.example.exergen.business.service.TimerPhase;

public class TimerSessionUseCase {
    private IntervalTimer intervalTimer;
    private TimerSessionObserver observer;

    public void initialize(int workSeconds, int restSeconds, int totalSets, TimerSessionObserver observer) {
        this.observer = observer;
        if (intervalTimer == null) {
            intervalTimer = new IntervalTimer(workSeconds, restSeconds, totalSets, new ForwardingObserver());
        }
    }

    public void startOrResume(int workSeconds, int restSeconds, int totalSets, TimerSessionObserver observer) {
        initialize(workSeconds, restSeconds, totalSets, observer);
        if (!intervalTimer.isRunning()) {
            intervalTimer.start();
        }
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

    public int getWorkDurationSeconds() {
        return intervalTimer != null ? intervalTimer.getWorkDurationSeconds() : 0;
    }

    public int getRestDurationSeconds() {
        return intervalTimer != null ? intervalTimer.getRestDurationSeconds() : 0;
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
            int workSeconds,
            int restSeconds,
            int totalSets,
            int currentSet,
            TimerMode mode,
            int remainingSeconds,
            boolean shouldBeRunning,
            TimerSessionObserver observer) {
        this.observer = observer;
        intervalTimer = new IntervalTimer(workSeconds, restSeconds, totalSets, new ForwardingObserver());
        intervalTimer.restoreState(
                currentSet,
                mode == TimerMode.REST ? TimerPhase.REST : TimerPhase.WORK,
                remainingSeconds);
        if (shouldBeRunning) {
            intervalTimer.start();
        }
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
