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
import com.example.exergen.business.usecase.StatisticsSummary;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.model.SessionRecord;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class StatsFragment extends Fragment {
    private SessionHistoryUseCase sessionHistoryUseCase;
    private StatisticsUseCase statisticsUseCase;
    private RecyclerView sessionHistoryRecyclerView;
    private TextView emptyStateText;
    private TextView totalSessionsValue;
    private TextView totalDurationValue;
    private TextView averageDurationValue;
    private TextView totalCaloriesValue;
    private TextView averageCaloriesValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sessionHistoryUseCase == null) {
            sessionHistoryUseCase = AppBootstrap.get().sessionHistoryUseCase;
        }
        if (statisticsUseCase == null) {
            statisticsUseCase = AppBootstrap.get().statisticsUseCase;
        }
    }

    public void setSessionHistoryUseCaseForTesting(SessionHistoryUseCase sessionHistoryUseCase) {
        this.sessionHistoryUseCase = sessionHistoryUseCase;
    }

    public void setStatisticsUseCaseForTesting(StatisticsUseCase statisticsUseCase) {
        this.statisticsUseCase = statisticsUseCase;
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
        totalSessionsValue = view.findViewById(R.id.stats_total_sessions_value);
        totalDurationValue = view.findViewById(R.id.stats_total_duration_value);
        averageDurationValue = view.findViewById(R.id.stats_average_duration_value);
        totalCaloriesValue = view.findViewById(R.id.stats_total_calories_value);
        averageCaloriesValue = view.findViewById(R.id.stats_average_calories_value);
        sessionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshOverallSummary();
        refreshSessionHistory();
    }

    private void refreshOverallSummary() {
        StatisticsSummary summary = statisticsUseCase.getOverallSummary();
        totalSessionsValue.setText(getString(R.string.stats_total_sessions_value_format, summary.getTotalSessions()));
        totalDurationValue.setText(getString(
                R.string.stats_total_duration_value_format,
                summary.getCumulativeDurationSeconds() / 60));
        averageDurationValue.setText(getString(
                R.string.stats_average_duration_value_format,
                summary.getAverageSessionLengthSeconds() / 60));
        totalCaloriesValue.setText(getString(
                R.string.stats_total_calories_value_format,
                summary.getTotalEstimatedCalories()));
        averageCaloriesValue.setText(getString(
                R.string.stats_average_calories_value_format,
                summary.getAverageEstimatedCalories()));
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