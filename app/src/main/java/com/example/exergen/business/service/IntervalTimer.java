package com.example.exergen.business.service;

import com.example.exergen.business.exception.InvalidTimerConfigurationException;
import com.example.exergen.business.exception.TimerAlreadyRunningException;

import java.util.Timer;
import java.util.TimerTask;

public class IntervalTimer {
    private static final long TICK_INTERVAL_MS = 1000L;
    private static final int TRANSITION_BUFFER_SECONDS = 1;

    private Timer timer;
    private final TimerObserver observer;

    private final int workDurationSeconds;
    private final int restDurationSeconds;
    private final int totalSets;

    private int currentSet = 1;
    private TimerPhase currentPhase = TimerPhase.WORK;
    private int remainingSeconds;
    private boolean isRunning = false;

    public IntervalTimer(int workSecs, int restSecs, int sets, TimerObserver observer) {
        if (workSecs <= 0) {
            throw new InvalidTimerConfigurationException("Work seconds must be > 0.");
        }
        if (restSecs < 0) {
            throw new InvalidTimerConfigurationException("Rest seconds must be >= 0.");
        }
        if (sets <= 0) {
            throw new InvalidTimerConfigurationException("Sets must be > 0.");
        }
        this.workDurationSeconds = workSecs;
        this.restDurationSeconds = restSecs;
        this.totalSets = sets;
        this.observer = observer;
        reset();
    }

    public void start() {
        if (isRunning) {
            throw new TimerAlreadyRunningException();
        }

        this.timer = new Timer();
        isRunning = true;

        if (observer != null) {
            observer.onPhaseChange(currentPhase);
            observer.onTick(remainingSeconds);
        }

        timer.schedule(new TimerTask() {
            public void run() {
                tick();
            }
        }, 0, TICK_INTERVAL_MS);
    }

    public void pause() {
        if (timer != null) {
            timer.cancel();
            timer = null; // Reset the reference so start() knows to make a new one
        }
        isRunning = false;
    }

    public void cancel() {
        pause();
        reset();
    }

    private void tick() {
        remainingSeconds--;
        if (remainingSeconds < 0) {
            handlePhaseSwitch();
            return;
        }
        if (isRunning && observer != null) {
            observer.onTick(remainingSeconds);
        }
    }

    private void handlePhaseSwitch() {
        if (currentPhase == TimerPhase.WORK) {
            if (restDurationSeconds > 0) {
                currentPhase = TimerPhase.REST;
                remainingSeconds = restDurationSeconds + TRANSITION_BUFFER_SECONDS;
                if (observer != null)
                    observer.onPhaseChange(TimerPhase.REST);
            } else {
                startNextSet();
            }
        } else {
            startNextSet();
        }
    }

    private void startNextSet() {
        currentSet++;
        if (currentSet > totalSets) {
            finish();
        } else {
            currentPhase = TimerPhase.WORK;
            remainingSeconds = workDurationSeconds + TRANSITION_BUFFER_SECONDS;
            if (observer != null)
                observer.onPhaseChange(TimerPhase.WORK);
        }
    }

    private void finish() {
        pause();
        if (observer != null)
            observer.onFinish();
    }

    public void reset() {
        currentSet = 1;
        currentPhase = TimerPhase.WORK;
        remainingSeconds = workDurationSeconds;
    }

    public int getWorkDurationSeconds() {
        return workDurationSeconds;
    }

    public int getRestDurationSeconds() {
        return restDurationSeconds;
    }

    public int getTotalSets() {
        return totalSets;
    }

    public int getCurrentSet() {
        return currentSet;
    }

    public TimerPhase getCurrentPhase() {
        return currentPhase;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void restoreState(int set, TimerPhase phase, int secondsRemaining) {
        this.currentSet = set;
        this.currentPhase = phase;
        this.remainingSeconds = secondsRemaining;
        this.isRunning = false;
    }

    public void skipPhase() {
        if (isRunning) {
            handlePhaseSwitch();
        } else {
            // If paused, switch phase but don't automatically start ticking
            handlePhaseSwitch();
            pause();
        }
    }
}