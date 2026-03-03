package com.example.exergen.business.service;

import java.util.Timer;
import java.util.TimerTask;

// Manages the core interval timer logic (work/rest cycles)
public class IntervalTimer {
    private Timer timer;
    private TimerObserver observer;

    private int workDurationSeconds;
    private int restDurationSeconds;
    private int totalSets;

    private int currentSet = 1;
    private boolean isWorkPhase = true;
    private int remainingSeconds;
    private boolean isRunning = false;

    public IntervalTimer(int workSecs, int restSecs, int sets, TimerObserver observer) {
        this.workDurationSeconds = workSecs;
        this.restDurationSeconds = restSecs;
        this.totalSets = sets;
        this.observer = observer;
        reset();
    }

    public void start() {
        if(isRunning) return;
        isRunning = true;

        // 1. Notify the observer of the phase (Work/Rest)
        if(observer != null) {
            observer.onPhaseChange(isWorkPhase);
            // 2. NEW: Immediately tell the UI to display the starting number (e.g., 30)
            // This stops the "00:00" flicker on start.
            observer.onTick(remainingSeconds);
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, 1000, 1000); // This delay is why the first tick took 1 second to appear
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
    }

    private void tick() {
        remainingSeconds--;

        // FIX: If we hit -1, switch phases so '0' only shows once
        if (remainingSeconds < 0) {
            handlePhaseSwitch();
        }

        if (isRunning && observer != null) {
            observer.onTick(remainingSeconds);
        }
    }

    private void handlePhaseSwitch() {
        if (isWorkPhase) {
            if (restDurationSeconds > 0) {
                isWorkPhase = false;
                remainingSeconds = restDurationSeconds;
                if (observer != null) observer.onPhaseChange(false);
            }
            else {
                startNextSet();
            }
        }
        else {
            startNextSet();
        }
    }

    private void startNextSet() {
        currentSet++;
        if (currentSet > totalSets) {
            finish();
        }
        else {
            isWorkPhase = true;
            remainingSeconds = workDurationSeconds;
            if (observer != null) observer.onPhaseChange(true);
        }
    }

    private void finish() {
        pause();
        if (observer != null) {
            observer.onFinish();
        }
    }

    public void reset() {
        currentSet = 1;
        isWorkPhase = true;
        remainingSeconds = workDurationSeconds;
    }
}