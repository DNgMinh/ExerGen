package com.example.exergen.business;

import java.util.Timer;
import java.util.TimerTask;

public class IntervalTimer {
    private Timer timer;
    private TimerObserver observer;

    //Settings/Config
    private int workDurationSeconds;
    private int restDurationSeconds;
    private int totalSets;

    //State
    private int currentSet = 1;
    private boolean isWorkPhase = true; //true = Work, false = Rest
    private int remainingSeconds;
    private boolean isRunning = false;

    //Constructor: Pass in the settings and the listener (observer)
    public IntervalTimer(int workSecs, int restSecs, int sets, TimerObserver observer) {
        this.workDurationSeconds = workSecs;
        this.restDurationSeconds = restSecs;
        this.totalSets = sets;
        this.observer = observer;
        reset(); //Set up the initial state
    }

    public void start() {
        if (isRunning)
            return; //Don't start if already running

        isRunning = true;
        timer = new Timer();
    }

    public void pause() {
        if (timer != null) {
            timer.cancel(); //Kill the timer thread
            timer = null;
        }
        isRunning = false;
    }

    //The logic that runs every second
    private void tick() {
        remainingSeconds--;

        if (remainingSeconds < 0) {
            handlePhaseSwitch();
        } else {
            //Update the text
            if (observer != null) {
                observer.onTick(remainingSeconds);
            }
        }
    }

    private void handlePhaseSwitch() {
        if (isWorkPhase) {
            //Work just finished. Time to Rest.
            if (restDurationSeconds > 0) {
                isWorkPhase = false;
                remainingSeconds = restDurationSeconds;
                if (observer != null) observer.onPhaseChange(false); //Tell UI: "REST MODE"
            } else {
                //If rest is 0 seconds, go to the next set
                startNextSet();
            }
        } else {
            //Rest just finished. Time to Work.
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
            if (observer != null)
                observer.onPhaseChange(true); //Tell UI: "WORK MODE"
        }
    }

    private void finish() {
        pause();
        if (observer != null)
            observer.onFinish();
    }

    public void reset() {
        pause();
        currentSet = 1;
        isWorkPhase = true;
        remainingSeconds = workDurationSeconds;

        //Update UI immediately so it doesn't show old numbers
        if (observer != null) {
            observer.onPhaseChange(true);
            observer.onTick(remainingSeconds);
        }
    }
}