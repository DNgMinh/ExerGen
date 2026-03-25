package com.example.exergen.business.usecase;

public interface TimerSessionObserver {
    void onTick(long secondsRemaining);

    void onModeChange(TimerMode mode);

    void onFinish();
}
