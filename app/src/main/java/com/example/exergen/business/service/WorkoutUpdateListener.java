package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;

public interface WorkoutUpdateListener {
    void onTick(int secondsRemaining);
    void onStateChanged(Exercise current, Exercise next, String phase);
    void onWorkoutFinished();
}
