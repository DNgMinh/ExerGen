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
import com.example.exergen.business.IntervalTimer;
import com.example.exergen.business.TimerObserver;

public class TimerFragment extends Fragment implements TimerObserver {

    //UI Components
    private TextView tvTimer, tvPhase;
    private Button btnStart, btnPause, btnStop;
    private LinearLayout pickerContainer;
    private NumberPicker npWork, npRest, npSets;

    //Logic
    private IntervalTimer intervalTimer;

    //---Fragment Lifecycle Methods---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        initializeViews(view);
        setupPickers();
        setupButtons();
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

    //---Setup Methods---

    private void setupPickers() {
        npWork.setMinValue(5); npWork.setMaxValue(60); npWork.setValue(30);
        npRest.setMinValue(5); npRest.setMaxValue(60); npRest.setValue(10);
        npSets.setMinValue(1); npSets.setMaxValue(20); npSets.setValue(3);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> startOrResumeTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnStop.setOnClickListener(v -> stopTimer());
    }

    //---User Action Handlers---

    private void startOrResumeTimer() {
        if (intervalTimer == null) {
            createNewTimer();
        }
        else {
            intervalTimer.start();
        }
        btnStart.setText(getString(R.string.btn_resume));
    }

    private void createNewTimer() {
        int work = npWork.getValue();
        int rest = npRest.getValue();
        int sets = npSets.getValue();

        intervalTimer = new IntervalTimer(work, rest, sets, this);
        intervalTimer.start();

        setSetupModeVisible(false);
    }

    private void pauseTimer() {
        if (intervalTimer != null) {
            intervalTimer.pause();
            tvPhase.setText(getString(R.string.timer_paused));
        }
    }

    private void stopTimer() {
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
        resetToDefaultState();
    }

    //---UI Helper Methods---

    private void setSetupModeVisible(boolean isVisible) {
        if (isVisible) {
            pickerContainer.setVisibility(View.VISIBLE);
            btnStart.setText(getString(R.string.btn_start));
        }
        else {
            pickerContainer.setVisibility(View.GONE);
        }
    }

    private void updateTimerText(long secondsRemaining) {
        String timeString = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);
        tvTimer.setText(timeString);
    }

    private void updatePhaseText(boolean isWorkPhase) {
        if (isWorkPhase) {
            tvPhase.setText(getString(R.string.timer_work));
            tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        } else {
            tvPhase.setText(getString(R.string.timer_rest));
            tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
        }
    }

    private void showDoneState() {
        tvPhase.setText(getString(R.string.timer_done));
    }

    private void resetToDefaultState() {
        // Safe to call from any thread because we wrap it
        safeRunOnUiThread(() -> {
            tvTimer.setText(getString(R.string.timer_default));
            tvPhase.setText(getString(R.string.timer_ready));
            setSetupModeVisible(true);
            intervalTimer = null;
        });
    }

    //---Observer Implementation (Callbacks from IntervalTimer)---

    @Override
    public void onTick(long secondsRemaining) {
        safeRunOnUiThread(() -> updateTimerText(secondsRemaining));
    }

    @Override
    public void onPhaseChange(boolean isWorkPhase) {
        safeRunOnUiThread(() -> updatePhaseText(isWorkPhase));
    }

    @Override
    public void onFinish() {
        resetToDefaultState();
        safeRunOnUiThread(() -> showDoneState());
    }

    private void safeRunOnUiThread(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }
}