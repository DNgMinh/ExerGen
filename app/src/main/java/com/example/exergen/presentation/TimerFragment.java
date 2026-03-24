package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.exergen.R;
import com.example.exergen.business.service.IntervalTimer;
import com.example.exergen.business.service.TimerPhase;
import com.example.exergen.business.service.TimerObserver;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.SessionRecord;

import java.util.UUID;

public class TimerFragment extends Fragment implements TimerObserver {
    private static final String KEY_WORK = "key_work";
    private static final String KEY_REST = "key_rest";
    private static final String KEY_SETS = "key_sets";
    private static final String KEY_HAS_TIMER = "key_has_timer";
    private static final String KEY_RUNNING = "key_running";
    private static final String KEY_TIMER_WORK = "key_timer_work";
    private static final String KEY_TIMER_REST = "key_timer_rest";
    private static final String KEY_TIMER_SETS = "key_timer_sets";
    private static final String KEY_TIMER_CURRENT_SET = "key_timer_current_set";
    private static final String KEY_TIMER_PHASE = "key_timer_phase";
    private static final String KEY_TIMER_REMAINING = "key_timer_remaining";

    private TextView tvTimer, tvPhase;
    private Button btnStart, btnPause, btnStop;
    private LinearLayout pickerContainer;
    private NumberPicker npWork, npRest, npSets;

    private IntervalTimer intervalTimer;
    private SoundFeedbackHelper soundFeedbackHelper;
    private boolean isTimerActive;
    private Bundle savedState;
    private SessionHistoryUseCase sessionHistoryUseCase;

    public void setDependencies(SessionHistoryUseCase sessionHistoryUseCase) {
        this.sessionHistoryUseCase = sessionHistoryUseCase;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sessionHistoryUseCase == null) {
            throw new IllegalStateException("TimerFragment dependencies not provided");
        }
        soundFeedbackHelper = new SoundFeedbackHelper();
        savedState = savedInstanceState;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        initializeViews(view);
        setupPickers();
        setupButtons();
        restoreUiState(savedState);

        return view;
    }

    private void initializeViews(View view) {
        tvTimer = view.findViewById(R.id.tv_timer);
        tvPhase = view.findViewById(R.id.tv_phase);
        btnStart = view.findViewById(R.id.btn_start);
        btnPause = view.findViewById(R.id.btn_pause);
        btnStop = view.findViewById(R.id.btn_stop);
        pickerContainer = view.findViewById(R.id.picker_container);
        npWork = view.findViewById(R.id.np_work);
        npRest = view.findViewById(R.id.np_rest);
        npSets = view.findViewById(R.id.np_sets);
    }

    private void setupPickers() {
        npWork.setMinValue(5);
        npWork.setMaxValue(60);
        npWork.setValue(30);
        npRest.setMinValue(5);
        npRest.setMaxValue(60);
        npRest.setValue(10);
        npSets.setMinValue(1);
        npSets.setMaxValue(20);
        npSets.setValue(3);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> startOrResumeTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnStop.setOnClickListener(v -> stopTimer());
        updateButtonStates();
    }

    private void startOrResumeTimer() {
        if (intervalTimer == null) {
            int work = npWork.getValue();
            int rest = npRest.getValue();
            int sets = npSets.getValue();

            intervalTimer = new IntervalTimer(work, rest, sets, this);
            setSetupModeVisible(false);
        }

        if (intervalTimer.isRunning()) {
            // Ignore duplicate start/resume taps while already active.
            return;
        }

        intervalTimer.start();
        isTimerActive = true;
        updateButtonStates();

        btnStart.setText(getString(R.string.btn_resume));
    }

    private void pauseTimer() {
        if (intervalTimer != null) {
            intervalTimer.pause();
            isTimerActive = false;
            updateButtonStates();
            tvPhase.setText(getString(R.string.timer_paused));
        }
    }

    private void stopTimer() {
        isTimerActive = false;
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
        resetToDefaultState();
        updateButtonStates();
    }

    private void setSetupModeVisible(boolean isVisible) {
        pickerContainer.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        // If setup is visible hide the stop button
        btnStop.setVisibility(isVisible ? View.GONE : View.VISIBLE);

        if (isVisible) {
            btnStart.setText(getString(R.string.btn_start));
        }
    }

    private void updateButtonStates() {
        btnPause.setEnabled(isTimerActive);
        btnStop.setEnabled(intervalTimer != null);
    }

    private void updateTimerText(long secondsRemaining) {
        String timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d",
                secondsRemaining / 60, secondsRemaining % 60);
        tvTimer.setText(timeString);
    }

    private void updatePhaseText(TimerPhase phase) {
        if (phase == TimerPhase.WORK) {
            tvPhase.setText(getString(R.string.timer_work));
            tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        } else {
            tvPhase.setText(getString(R.string.timer_rest));
            tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
        }
    }

    private void resetToDefaultState() {
        safeRunOnUiThread(() -> {
            tvTimer.setText(getString(R.string.timer_default));
            tvPhase.setText(getString(R.string.timer_ready));
            setSetupModeVisible(true);
            intervalTimer = null;
            isTimerActive = false;
            updateButtonStates();
        });
    }

    @Override
    public void onTick(long secondsRemaining) {
        safeRunOnUiThread(() -> {
            updateTimerText(secondsRemaining);
            // Play beeps on 3, 2, 1
            if (isTimerActive && secondsRemaining > 0 && secondsRemaining <= 3) {
                soundFeedbackHelper.playCountdownBeep();
            }
        });
    }

    @Override
    public void onPhaseChange(TimerPhase phase) {
        safeRunOnUiThread(() -> {
            updatePhaseText(phase);
            // Transition beep
            if (isTimerActive) {
                soundFeedbackHelper.playTransitionBeep();
            }
        });
    }

    @Override
    public void onFinish() {
        saveCompletedTimerSession();
        isTimerActive = false;
        resetToDefaultState();
    }

    private void saveCompletedTimerSession() {
        if (intervalTimer == null) {
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

    private void safeRunOnUiThread(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundFeedbackHelper != null) {
            soundFeedbackHelper.release();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_WORK, npWork.getValue());
        outState.putInt(KEY_REST, npRest.getValue());
        outState.putInt(KEY_SETS, npSets.getValue());

        boolean hasTimer = intervalTimer != null;
        outState.putBoolean(KEY_HAS_TIMER, hasTimer);
        if (hasTimer) {
            outState.putBoolean(KEY_RUNNING, intervalTimer.isRunning());
            outState.putInt(KEY_TIMER_WORK, intervalTimer.getWorkDurationSeconds());
            outState.putInt(KEY_TIMER_REST, intervalTimer.getRestDurationSeconds());
            outState.putInt(KEY_TIMER_SETS, intervalTimer.getTotalSets());
            outState.putInt(KEY_TIMER_CURRENT_SET, intervalTimer.getCurrentSet());
            outState.putString(KEY_TIMER_PHASE, intervalTimer.getCurrentPhase().name());
            outState.putInt(KEY_TIMER_REMAINING, intervalTimer.getRemainingSeconds());
            intervalTimer.pause();
        }
    }

    private void restoreUiState(@Nullable Bundle state) {
        if (state == null) {
            setSetupModeVisible(true);
            updateButtonStates();
            return;
        }

        npWork.setValue(state.getInt(KEY_WORK, npWork.getValue()));
        npRest.setValue(state.getInt(KEY_REST, npRest.getValue()));
        npSets.setValue(state.getInt(KEY_SETS, npSets.getValue()));

        boolean hasTimer = state.getBoolean(KEY_HAS_TIMER, false);
        if (!hasTimer) {
            setSetupModeVisible(true);
            updateButtonStates();
            return;
        }

        intervalTimer = new IntervalTimer(
                state.getInt(KEY_TIMER_WORK, npWork.getValue()),
                state.getInt(KEY_TIMER_REST, npRest.getValue()),
                state.getInt(KEY_TIMER_SETS, npSets.getValue()),
                this);

        TimerPhase phase = TimerPhase.valueOf(state.getString(KEY_TIMER_PHASE, TimerPhase.WORK.name()));
        intervalTimer.restoreState(
                state.getInt(KEY_TIMER_CURRENT_SET, 1),
                phase,
                state.getInt(KEY_TIMER_REMAINING, intervalTimer.getRemainingSeconds()));

        setSetupModeVisible(false);
        updateTimerText(intervalTimer.getRemainingSeconds());
        updatePhaseText(phase);

        if (state.getBoolean(KEY_RUNNING, false)) {
            isTimerActive = true;
            intervalTimer.start();
            btnStart.setText(getString(R.string.btn_resume));
        } else {
            isTimerActive = false;
        }
        updateButtonStates();
    }
}
