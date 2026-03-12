package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.SessionRecord;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class StatsFragment extends Fragment {
    private SessionHistoryUseCase sessionHistoryUseCase;
    private RecyclerView sessionHistoryRecyclerView;
    private TextView emptyStateText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sessionHistoryUseCase == null) {
            sessionHistoryUseCase = AppBootstrap.get().sessionHistoryUseCase;
        }
    }

    public void setSessionHistoryUseCaseForTesting(SessionHistoryUseCase sessionHistoryUseCase) {
        this.sessionHistoryUseCase = sessionHistoryUseCase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionHistoryRecyclerView = view.findViewById(R.id.session_history_recycler_view);
        emptyStateText = view.findViewById(R.id.session_history_empty_text);
        sessionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSessionHistory();
    }

    private void refreshSessionHistory() {
        List<SessionRecord> sessions = sessionHistoryUseCase.getSessionHistory();
        if (sessions == null || sessions.isEmpty()) {
            sessionHistoryRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            return;
        }

        emptyStateText.setVisibility(View.GONE);
        sessionHistoryRecyclerView.setVisibility(View.VISIBLE);
        sessionHistoryRecyclerView.setAdapter(new SessionHistoryAdapter(sessions, this::showSessionDetails));
    }

    private void showSessionDetails(SessionRecord selectedItem) {
        if (selectedItem == null || getContext() == null) {
            return;
        }

        SessionRecord record = sessionHistoryUseCase.getSessionById(selectedItem.getId());
        if (record == null) {
            return;
        }

        String completedAtText = DateFormat.getDateTimeInstance().format(new Date(record.getCompletedAtEpochMs()));
        String message = getString(
                R.string.session_history_detail_format,
                record.getWorkoutName(),
                completedAtText,
                record.getTotalDurationSeconds(),
                record.getExerciseCount(),
                record.getRoundsCompleted(),
                record.getRoundsPlanned());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.session_history_detail_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}