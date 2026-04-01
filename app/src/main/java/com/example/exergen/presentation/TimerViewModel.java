package com.example.exergen.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.TimerMode;
import com.example.exergen.business.usecase.TimerSessionObserver;
import com.example.exergen.business.usecase.TimerSessionUseCase;
import com.example.exergen.model.SessionRecord;

import java.util.List;
import java.util.UUID;

public class TimerViewModel extends ViewModel implements TimerSessionObserver {
    private final MutableLiveData<Integer> secondsRemaining = new MutableLiveData<>();
    private final MutableLiveData<TimerMode> phase = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasTimer = new MutableLiveData<>(false);

    private final TimerSessionUseCase timerSessionUseCase = new TimerSessionUseCase();
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;

    public void init(
            SessionHistoryUseCase sessionHistoryUseCase,
            CaloriesEstimationUseCase caloriesEstimationUseCase) {
        if (this.sessionHistoryUseCase == null) {
            this.sessionHistoryUseCase = sessionHistoryUseCase;
        }
        if (this.caloriesEstimationUseCase == null) {
            this.caloriesEstimationUseCase = caloriesEstimationUseCase;
        }
    }

    public LiveData<Integer> getSecondsRemaining() {
        return secondsRemaining;
    }

    public LiveData<TimerMode> getPhase() {
        return phase;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public LiveData<Boolean> getHasTimer() {
        return hasTimer;
    }

    public void startOrResume(int workSeconds, int restSeconds, int totalSets) {
        if (!timerSessionUseCase.hasActiveSession()) {
            hasTimer.setValue(true);
            phase.setValue(TimerMode.WORK); // Initial state
            secondsRemaining.setValue(workSeconds);
        }
        timerSessionUseCase.startOrResume(List.of(workSeconds), List.of(restSeconds), totalSets, this);
        isRunning.setValue(true);
    }

    public void pause() {
        if (timerSessionUseCase.isRunning()) {
            timerSessionUseCase.pause();
            isRunning.setValue(false);
        }
    }

    public void stop() {
        timerSessionUseCase.stop();
        isRunning.setValue(false);
        hasTimer.setValue(false);
    }

    public boolean hasActiveTimer() {
        return timerSessionUseCase.hasActiveSession();
    }

    public boolean isTimerRunning() {
        return timerSessionUseCase.isRunning();
    }

    public int getWorkDurationSeconds() {
        List<Integer> durs = timerSessionUseCase.getWorkDurations();
        return (durs != null && !durs.isEmpty()) ? durs.get(0) : 0;
    }

    public int getRestDurationSeconds() {
        List<Integer> durs = timerSessionUseCase.getRestDurations();
        return (durs != null && !durs.isEmpty()) ? durs.get(0) : 0;
    }

    public int getTotalSets() {
        return timerSessionUseCase.getTotalSets();
    }

    public int getCurrentSet() {
        return timerSessionUseCase.getCurrentSet();
    }

    public TimerMode getCurrentPhase() {
        return timerSessionUseCase.getCurrentMode();
    }

    public int getRemainingSeconds() {
        return timerSessionUseCase.getRemainingSeconds();
    }

    public void restoreTimerState(
            int workSeconds,
            int restSeconds,
            int totalSets,
            int currentSet,
            TimerMode phase,
            int remainingSeconds,
            boolean shouldBeRunning) {
        timerSessionUseCase.restoreState(
                List.of(workSeconds),
                List.of(restSeconds),
                totalSets,
                currentSet,
                phase,
                remainingSeconds,
                shouldBeRunning,
                this);
        hasTimer.setValue(true);
        this.phase.setValue(phase);
        this.secondsRemaining.setValue(remainingSeconds);
        isRunning.setValue(shouldBeRunning);
    }

    @Override
    public void onTick(long secondsRemaining) {
        this.secondsRemaining.postValue((int) secondsRemaining);
    }

    @Override
    public void onModeChange(TimerMode phase) {
        this.phase.postValue(phase);
    }

    @Override
    public void onFinish() {
        saveCompletedTimerSession();
        timerSessionUseCase.stop();
        isRunning.postValue(false);
        hasTimer.postValue(false);
    }

    private void saveCompletedTimerSession() {
        if (sessionHistoryUseCase == null || !timerSessionUseCase.hasActiveSession()) {
            return;
        }
        
        int work = getWorkDurationSeconds();
        int rest = getRestDurationSeconds();
        
        SessionRecord sessionRecord = buildSessionRecordForCompletedTimer(
                work,
                rest,
                timerSessionUseCase.getTotalSets(),
                caloriesEstimationUseCase,
                System.currentTimeMillis(),
                "session-" + UUID.randomUUID());
        sessionHistoryUseCase.saveCompletedSession(sessionRecord);
    }

    static SessionRecord buildSessionRecordForCompletedTimer(
            int workSeconds,
            int restSeconds,
            int totalSets,
            CaloriesEstimationUseCase caloriesEstimationUseCase,
            long completedAtEpochMs,
            String sessionId) {
        int totalDurationSeconds = totalSets * (workSeconds + restSeconds);
        int estimatedCalories = caloriesEstimationUseCase == null
                ? SessionRecord.UNKNOWN_ESTIMATED_CALORIES
                : caloriesEstimationUseCase.estimateCaloriesWithDefaultIntensity(totalDurationSeconds);
        return new SessionRecord(
                sessionId,
                "manual-timer",
                "Manual Interval Timer",
                completedAtEpochMs,
                totalDurationSeconds,
                1,
                totalSets,
                totalSets,
                estimatedCalories);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        timerSessionUseCase.stop();
    }
}
