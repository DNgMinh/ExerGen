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

    private TextView tvTimer, tvPhase;
    private Button btnStart, btnPause, btnStop;
    private LinearLayout pickerContainer;
    private NumberPicker npWork, npRest, npSets;
    private IntervalTimer intervalTimer;

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

    private void setupPickers() {
        npWork.setMinValue(5); npWork.setMaxValue(60); npWork.setValue(30);
        npRest.setMinValue(5); npRest.setMaxValue(60); npRest.setValue(10);
        npSets.setMinValue(1); npSets.setMaxValue(20); npSets.setValue(3);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> handleStartClick());
        btnPause.setOnClickListener(v -> {
            if (intervalTimer != null) {
                intervalTimer.pause();
                tvPhase.setText(getString(R.string.timer_paused));
            }
        });
        btnStop.setOnClickListener(v -> {
            if (intervalTimer != null) {
                intervalTimer.cancel();
                intervalTimer = null;
            }
            resetUI();
        });
    }

    private void handleStartClick() {
        if (intervalTimer == null) {
            int work = npWork.getValue();
            int rest = npRest.getValue();
            int sets = npSets.getValue();
            intervalTimer = new IntervalTimer(work, rest, sets, this);
            intervalTimer.start();
            pickerContainer.setVisibility(View.GONE);
            btnStart.setText(getString(R.string.btn_resume));
        } else {
            intervalTimer.start();
        }
    }

    private void resetUI() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvTimer.setText(getString(R.string.timer_default));
                tvPhase.setText(getString(R.string.timer_ready));
                pickerContainer.setVisibility(View.VISIBLE);
                btnStart.setText(getString(R.string.btn_start));
                intervalTimer = null;
            });
        }
    }

    @Override
    public void onTick(long secondsRemaining) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                String time = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);
                tvTimer.setText(time);
            });
        }
    }

    @Override
    public void onPhaseChange(boolean isWorkPhase) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (isWorkPhase) {
                    tvPhase.setText(getString(R.string.timer_work));
                    tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                } else {
                    tvPhase.setText(getString(R.string.timer_rest));
                    tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
                }
            });
        }
    }

    @Override
    public void onFinish() {
        resetUI();
        if(getActivity() != null) {
            getActivity().runOnUiThread(() -> tvPhase.setText(getString(R.string.timer_done)));
        }
    }
}