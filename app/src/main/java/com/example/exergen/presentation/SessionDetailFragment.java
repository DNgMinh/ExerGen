package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.exergen.R;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.SessionRecord;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.DateFormat;
import java.util.Date;

public class SessionDetailFragment extends BottomSheetDialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private SessionHistoryUseCase sessionHistoryUseCase;

    public static SessionDetailFragment newInstance(String sessionId) {
        SessionDetailFragment fragment = new SessionDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDependencies(SessionHistoryUseCase sessionHistoryUseCase) {
        this.sessionHistoryUseCase = sessionHistoryUseCase;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String sessionId = getArguments() != null ? getArguments().getString(ARG_SESSION_ID) : null;
        if (sessionId == null || sessionHistoryUseCase == null) {
            dismiss();
            return;
        }

        SessionRecord record = sessionHistoryUseCase.getSessionById(sessionId);
        if (record == null) {
            dismiss();
            return;
        }

        TextView workoutName = view.findViewById(R.id.session_detail_workout_name);
        TextView dateText = view.findViewById(R.id.session_detail_date);
        TextView durationText = view.findViewById(R.id.session_detail_duration);
        TextView caloriesText = view.findViewById(R.id.session_detail_calories);
        TextView exercisesText = view.findViewById(R.id.session_detail_exercises);
        TextView setsText = view.findViewById(R.id.session_detail_sets);
        Button closeButton = view.findViewById(R.id.btn_session_detail_close);

        workoutName.setText(record.getWorkoutName());
        dateText.setText(DateFormat.getDateTimeInstance().format(new Date(record.getCompletedAtEpochMs())));
        durationText.setText(record.getTotalDurationSeconds() + " sec");
        
        if (record.hasEstimatedCalories()) {
            caloriesText.setText(record.getEstimatedCalories() + " kcal");
        } else {
            caloriesText.setText(R.string.session_history_calories_unavailable);
        }

        exercisesText.setText(String.valueOf(record.getExerciseCount()));
        setsText.setText(record.getSetsCompleted() + "/" + record.getSetsPlanned());

        closeButton.setOnClickListener(v -> dismiss());
    }
}
