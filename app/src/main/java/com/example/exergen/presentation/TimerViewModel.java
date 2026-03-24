package com.example.exergen.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.exergen.business.service.IntervalTimer;
import com.example.exergen.business.service.TimerObserver;
import com.example.exergen.business.service.TimerPhase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.SessionRecord;

import java.util.UUID;

public class TimerViewModel extends ViewModel implements TimerObserver {
    private final MutableLiveData<Integer> secondsRemaining = new MutableLiveData<>();
    private final MutableLiveData<TimerPhase> phase = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasTimer = new MutableLiveData<>(false);

    private IntervalTimer intervalTimer;
    private SessionHistoryUseCase sessionHistoryUseCase;

    public void init(SessionHistoryUseCase sessionHistoryUseCase) {
        if (this.sessionHistoryUseCase == null) {
            this.sessionHistoryUseCase = sessionHistoryUseCase;
        }
    }

    public LiveData<Integer> getSecondsRemaining() {
        return secondsRemaining;
    }

    public LiveData<TimerPhase> getPhase() {
        return phase;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public LiveData<Boolean> getHasTimer() {
        return hasTimer;
    }

    public void startOrResume(int workSeconds, int restSeconds, int totalSets) {
        if (intervalTimer == null) {
            intervalTimer = new IntervalTimer(workSeconds, restSeconds, totalSets, this);
            hasTimer.setValue(true);
            phase.setValue(intervalTimer.getCurrentPhase());
            secondsRemaining.setValue(intervalTimer.getRemainingSeconds());
        }

        if (intervalTimer.isRunning()) {
            return;
        }

        intervalTimer.start();
        isRunning.setValue(true);
    }

    public void pause() {
        if (intervalTimer != null && intervalTimer.isRunning()) {
            intervalTimer.pause();
            isRunning.setValue(false);
        }
    }

    public void stop() {
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
        isRunning.setValue(false);
        hasTimer.setValue(false);
    }

    public boolean hasActiveTimer() {
        return intervalTimer != null;
    }

    public boolean isTimerRunning() {
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

    public TimerPhase getCurrentPhase() {
        return intervalTimer != null ? intervalTimer.getCurrentPhase() : TimerPhase.WORK;
    }

    public int getRemainingSeconds() {
        return intervalTimer != null ? intervalTimer.getRemainingSeconds() : 0;
    }

    public void restoreTimerState(
            int workSeconds,
            int restSeconds,
            int totalSets,
            int currentSet,
            TimerPhase phase,
            int remainingSeconds,
            boolean shouldBeRunning) {
        intervalTimer = new IntervalTimer(workSeconds, restSeconds, totalSets, this);
        intervalTimer.restoreState(currentSet, phase, remainingSeconds);
        hasTimer.setValue(true);
        this.phase.setValue(phase);
        this.secondsRemaining.setValue(remainingSeconds);

        if (shouldBeRunning) {
            intervalTimer.start();
            isRunning.setValue(true);
        } else {
            isRunning.setValue(false);
        }
    }

    @Override
    public void onTick(long secondsRemaining) {
        this.secondsRemaining.postValue((int) secondsRemaining);
    }

    @Override
    public void onPhaseChange(TimerPhase phase) {
        this.phase.postValue(phase);
    }

    @Override
    public void onFinish() {
        saveCompletedTimerSession();
        intervalTimer = null;
        isRunning.postValue(false);
        hasTimer.postValue(false);
    }

    private void saveCompletedTimerSession() {
        if (sessionHistoryUseCase == null || intervalTimer == null) {
            return;
        }
        SessionRecord sessionRecord = buildSessionRecordForCompletedTimer(
                intervalTimer.getWorkDurationSeconds(),
                intervalTimer.getRestDurationSeconds(),
                intervalTimer.getTotalSets(),
                System.currentTimeMillis(),
                "session-" + UUID.randomUUID());
        sessionHistoryUseCase.saveCompletedSession(sessionRecord);
    }

    static SessionRecord buildSessionRecordForCompletedTimer(
            int workSeconds,
            int restSeconds,
            int totalSets,
            long completedAtEpochMs,
            String sessionId) {
        int totalDurationSeconds = totalSets * (workSeconds + restSeconds);
        return new SessionRecord(
                sessionId,
                "manual-timer",
                "Manual Interval Timer",
                completedAtEpochMs,
                totalDurationSeconds,
                1,
                totalSets,
                totalSets);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
    }
}
