package com.example.exergen.business.service;

public interface TimerObserver {
    //1. Called every second to update the countdown (e.g., "00:29")
    void onTick(long secondsRemaining);

    //2. Called when we switch from WORK to REST (or vice versa)
    void onPhaseChange(boolean isWorkPhase);

    //3. Called when the whole workout is finished
    void onFinish();
}