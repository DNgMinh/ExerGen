package com.example.exergen.business;

import java.util.Timer;
import java.util.TimerTask;

public class IntervalTimer {
    private Timer timer;
    private TimerObserver observer;

    // Settings
    private int workDurationSeconds;
    private int restDurationSeconds;
    private int totalSets;

    // State
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
        if(isRunning)
            return;

        isRunning = true;

        if(observer != null)
            observer.onPhaseChange(isWorkPhase);

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, 1000, 1000); // Delay 1 sec, then repeat every 1 sec
    }

    public void pause() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isRunning = false;
    }

    //Call this to kill the timer completely (Zombie fix)
    public void cancel() {
        pause();
    }

    private void tick() {
        remainingSeconds--;

        if (remainingSeconds < 0) {
            handlePhaseSwitch();
        } else {
            if (observer != null) {
                // Determine if we need to switch phases or just update UI
                observer.onTick(remainingSeconds);
            }
        }
    }

    private void handlePhaseSwitch() {
        if (isWorkPhase) {
            //Work finished. Switch to Rest.
            if (restDurationSeconds > 0) {
                isWorkPhase = false;
                remainingSeconds = restDurationSeconds;
                if (observer != null) observer.onPhaseChange(false); //Rest
            } else {
                startNextSet();
            }
        } else {
            //Rest finished. Switch to Work (Next Set).
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
            if (observer != null) observer.onPhaseChange(true); //Work
        }
    }

    private void finish() {
        pause();
        if (observer != null) {
            observer.onTick(0);
            observer.onFinish();
        }
    }

    public void reset() {
        pause();
        currentSet = 1;
        isWorkPhase = true;
        remainingSeconds = workDurationSeconds;

        if (observer != null) {
            observer.onPhaseChange(true);
            observer.onTick(remainingSeconds);
        }
    }
}