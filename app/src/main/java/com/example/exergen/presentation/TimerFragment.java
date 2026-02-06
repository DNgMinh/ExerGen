package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exergen.R;
import com.example.exergen.business.IntervalTimer;
import com.example.exergen.business.TimerObserver;

public class TimerFragment extends Fragment implements TimerObserver {

    private TextView tvTimer;
    private TextView tvPhase;
    private Button btnStart;
    private Button btnPause;
    private EditText etWork;//Work Duration
    private EditText etRest;//Rest Duration
    private EditText etSets;//Number of Sets

    private IntervalTimer intervalTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout we just created
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        //Buttons
        tvTimer = view.findViewById(R.id.tv_timer);
        tvPhase = view.findViewById(R.id.tv_phase);
        btnStart = view.findViewById(R.id.btn_start);
        btnPause = view.findViewById(R.id.btn_pause);

        //Interval Settings
        etWork = view.findViewById(R.id.et_work);
        etRest = view.findViewById(R.id.et_rest);
        etSets = view.findViewById(R.id.et_sets);

        //Set Button Listeners
        btnStart.setOnClickListener(v -> intervalTimer.start());
        btnPause.setOnClickListener(v -> intervalTimer.pause());

        btnStart.setOnClickListener(v -> {
            //If the timer doesn't exist (fresh start) or is finished...
            if (intervalTimer == null) {
                startNewWorkout();
            } else {
                //If it exists, it might be paused, so just resume it
                intervalTimer.start();
            }
        });

        return view;
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
                    tvPhase.setText("WORK!");
                    tvPhase.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    tvPhase.setText("REST");
                    tvPhase.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                }
            });
        }
    }

    @Override
    public void onFinish() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvTimer.setText("DONE");
                tvPhase.setText("Tap Start to Reset");

                intervalTimer = null;
            });
        }
    }

    private void startNewWorkout() {
        //Get the text from the boxes
        String workText = etWork.getText().toString();
        String restText = etRest.getText().toString();
        String setsText = etSets.getText().toString();

        //Convert to numbers (integers)
        int work = Integer.parseInt(workText);
        int rest = Integer.parseInt(restText);
        int sets = Integer.parseInt(setsText);

        //Create the timer logic with user input
        intervalTimer = new IntervalTimer(work, rest, sets, this);
        intervalTimer.start();

    }
}