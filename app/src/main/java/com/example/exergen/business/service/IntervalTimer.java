package com.example.exergen.business.service;

import com.example.exergen.business.exception.TimerAlreadyRunningException;
import com.example.exergen.business.validation.ValidationHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IntervalTimer {
    private static final long TICK_INTERVAL_MS = 1000L;
    private static final int TRANSITION_BUFFER_SECONDS = 1;

    private Timer timer;
    private final TimerObserver observer;

    private final List<Integer> workDurations;
    private final List<Integer> restDurations;
    private final int totalSets;

    private int currentSet = 1;
    private TimerPhase currentPhase = TimerPhase.WORK;
    private int remainingSeconds;
    private boolean isRunning = false;

    public IntervalTimer(List<Integer> workSecs, List<Integer> restSecs, int sets, TimerObserver observer) {
        if (workSecs == null || workSecs.isEmpty()) throw new IllegalArgumentException("workSecs required");
        if (restSecs == null || restSecs.isEmpty()) throw new IllegalArgumentException("restSecs required");
        
        for (int work : workSecs) {
            ValidationHelper.requirePositive(work, "Work seconds must be > 0.");
        }
        for (int rest : restSecs) {
            ValidationHelper.requireNonNegative(rest, "Rest seconds must be >= 0.");
        }

        ValidationHelper.requirePositive(sets, "Sets must be > 0.");
        
        this.workDurations = List.copyOf(workSecs);
        this.restDurations = List.copyOf(restSecs);
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
            timer = null;
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
            int rest = getRestForCurrentStep();
            if (rest > 0) {
                currentPhase = TimerPhase.REST;
                remainingSeconds = rest; // Remove buffer for cleaner transitions
                if (observer != null) {
                    observer.onPhaseChange(TimerPhase.REST);
                    observer.onTick(remainingSeconds);
                }
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
            remainingSeconds = getWorkForCurrentStep();
            if (observer != null) {
                observer.onPhaseChange(TimerPhase.WORK);
                observer.onTick(remainingSeconds);
            }
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
        remainingSeconds = getWorkForCurrentStep();
    }

    private int getWorkForCurrentStep() {
        return workDurations.get((currentSet - 1) % workDurations.size());
    }

    private int getRestForCurrentStep() {
        return restDurations.get((currentSet - 1) % restDurations.size());
    }

    public List<Integer> getWorkDurations() { return workDurations; }
    public List<Integer> getRestDurations() { return restDurations; }
    public int getTotalSets() { return totalSets; }
    public int getCurrentSet() { return currentSet; }
    public TimerPhase getCurrentPhase() { return currentPhase; }
    public int getRemainingSeconds() { return remainingSeconds; }
    public boolean isRunning() { return isRunning; }

    public void restoreState(int set, TimerPhase phase, int secondsRemaining) {
        this.currentSet = set;
        this.currentPhase = phase;
        this.remainingSeconds = secondsRemaining;
        this.isRunning = false;
    }

    public void skipPhase() {
        handlePhaseSwitch();
        if (!isRunning) {
            pause();
        }
    }
}
