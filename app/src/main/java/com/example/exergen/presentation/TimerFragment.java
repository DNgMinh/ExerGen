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
import androidx.lifecycle.ViewModelProvider;

import com.example.exergen.R;
import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.TimerMode;
import com.example.exergen.business.service.CaloriesEstimationService;
import com.example.exergen.model.SessionRecord;

public class TimerFragment extends Fragment {
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

    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;
    private SoundFeedbackHelper soundFeedbackHelper;
    private TimerViewModel viewModel;

    public void setDependencies(
            SessionHistoryUseCase sessionHistoryUseCase,
            CaloriesEstimationUseCase caloriesEstimationUseCase) {
        this.sessionHistoryUseCase = sessionHistoryUseCase;
        this.caloriesEstimationUseCase = caloriesEstimationUseCase;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sessionHistoryUseCase == null || caloriesEstimationUseCase == null) {
            throw new IllegalStateException("TimerFragment dependencies not provided");
        }
        soundFeedbackHelper = new SoundFeedbackHelper();
        viewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        viewModel.init(sessionHistoryUseCase, caloriesEstimationUseCase);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        initializeViews(view);
        setupPickers();
        setupButtons();
        setupObservers();
        restoreUiState(savedInstanceState);
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
        btnStart.setOnClickListener(v -> {
            viewModel.startOrResume(npWork.getValue(), npRest.getValue(), npSets.getValue());
            btnStart.setText(getString(R.string.btn_resume));
        });
        btnPause.setOnClickListener(v -> viewModel.pause());
        btnStop.setOnClickListener(v -> viewModel.stop());
        updateButtonStates();
    }

    private void setupObservers() {
        viewModel.getSecondsRemaining().observe(getViewLifecycleOwner(), seconds -> {
            updateTimerText(seconds);
            if (viewModel.isTimerRunning() && seconds > 0 && seconds <= 3) {
                soundFeedbackHelper.playCountdownBeep();
            }
        });

        viewModel.getPhase().observe(getViewLifecycleOwner(), phase -> {
            updatePhaseText(phase);
            if (viewModel.isTimerRunning()) {
                soundFeedbackHelper.playTransitionBeep();
            }
        });

        viewModel.getHasTimer().observe(getViewLifecycleOwner(), hasTimer -> {
            boolean active = Boolean.TRUE.equals(hasTimer);
            setSetupModeVisible(!active);
            if (!active) {
                resetToDefaultState();
            }
            updateButtonStates();
        });

        viewModel.getIsRunning().observe(getViewLifecycleOwner(), running -> {
            if (!Boolean.TRUE.equals(running) && viewModel.hasActiveTimer()) {
                tvPhase.setText(getString(R.string.timer_paused));
            }
            updateButtonStates();
        });
    }

    private void setSetupModeVisible(boolean isVisible) {
        pickerContainer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        btnStop.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        if (isVisible) {
            btnStart.setText(getString(R.string.btn_start));
        }
    }

    private void updateButtonStates() {
        btnPause.setEnabled(viewModel != null && viewModel.isTimerRunning());
        btnStop.setEnabled(viewModel != null && viewModel.hasActiveTimer());
    }

    private void updateTimerText(long secondsRemaining) {
        String timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d",
                secondsRemaining / 60, secondsRemaining % 60);
        tvTimer.setText(timeString);
    }

    private void updatePhaseText(TimerMode phase) {
        if (phase == null) {
            return;
        }
        if (phase == TimerMode.WORK) {
            tvPhase.setText(getString(R.string.timer_work));
            tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        } else {
            tvPhase.setText(getString(R.string.timer_rest));
            tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
        }
    }

    private void resetToDefaultState() {
        tvTimer.setText(getString(R.string.timer_default));
        tvPhase.setText("");
        btnStart.setText(getString(R.string.btn_start));
        setSetupModeVisible(true);
        updateButtonStates();
    }

    static SessionRecord buildSessionRecordForCompletedTimer(
            int workSeconds,
            int restSeconds,
            int totalSets,
            long completedAtEpochMs,
            String sessionId) {
        return TimerViewModel.buildSessionRecordForCompletedTimer(
                workSeconds,
                restSeconds,
                totalSets,
                new CaloriesEstimationUseCase(new CaloriesEstimationService()),
                completedAtEpochMs,
                sessionId);
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

        boolean hasTimer = viewModel != null && viewModel.hasActiveTimer();
        outState.putBoolean(KEY_HAS_TIMER, hasTimer);
        if (hasTimer) {
            outState.putBoolean(KEY_RUNNING, viewModel.isTimerRunning());
            outState.putInt(KEY_TIMER_WORK, viewModel.getWorkDurationSeconds());
            outState.putInt(KEY_TIMER_REST, viewModel.getRestDurationSeconds());
            outState.putInt(KEY_TIMER_SETS, viewModel.getTotalSets());
            outState.putInt(KEY_TIMER_CURRENT_SET, viewModel.getCurrentSet());
            outState.putString(KEY_TIMER_PHASE, viewModel.getCurrentPhase().name());
            outState.putInt(KEY_TIMER_REMAINING, viewModel.getRemainingSeconds());
            viewModel.pause();
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

        TimerMode phase = TimerMode.valueOf(state.getString(KEY_TIMER_PHASE, TimerMode.WORK.name()));
        viewModel.restoreTimerState(
                state.getInt(KEY_TIMER_WORK, npWork.getValue()),
                state.getInt(KEY_TIMER_REST, npRest.getValue()),
                state.getInt(KEY_TIMER_SETS, npSets.getValue()),
                state.getInt(KEY_TIMER_CURRENT_SET, 1),
                phase,
                state.getInt(KEY_TIMER_REMAINING, npWork.getValue()),
                state.getBoolean(KEY_RUNNING, false));

        setSetupModeVisible(false);
        updateTimerText(viewModel.getRemainingSeconds());
        updatePhaseText(phase);
        btnStart.setText(getString(R.string.btn_resume));
        updateButtonStates();
    }
}
