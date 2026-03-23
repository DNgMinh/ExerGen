package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.example.exergen.model.StatisticsSummary;
import com.example.exergen.business.usecase.StatisticsTimeRange;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.model.WeeklyTrendPoint;
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
    private TextView trendValue;
    private Spinner timeRangeSpinner;
    private StatisticsTimeRange selectedTimeRange = StatisticsTimeRange.ALL_TIME;

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
        trendValue = view.findViewById(R.id.stats_trend_value);
        timeRangeSpinner = view.findViewById(R.id.stats_time_range_spinner);
        sessionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.stats_time_range_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeRangeSpinner.setAdapter(adapter);
        timeRangeSpinner.setSelection(StatisticsTimeRange.ALL_TIME.getSpinnerPosition(), false);
        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeRange = StatisticsTimeRange.fromSpinnerPosition(position);
                refreshStatisticsSection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTimeRange = StatisticsTimeRange.ALL_TIME;
                refreshStatisticsSection();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStatisticsSection();
        refreshSessionHistory();
    }

    private void refreshStatisticsSection() {
        refreshOverallSummary();
        refreshTrendSection();
    }

    private void refreshOverallSummary() {
        StatisticsSummary summary = statisticsUseCase.getSummaryForTimeRange(selectedTimeRange);
        totalSessionsValue.setText(getString(R.string.stats_total_sessions_value_format, summary.getTotalSessions()));
        int totalMinutes = summary.getCumulativeDurationSeconds() / 60;
        int totalSeconds = summary.getCumulativeDurationSeconds() % 60;
        int averageMinutes = summary.getAverageSessionLengthSeconds() / 60;
        int averageSeconds = summary.getAverageSessionLengthSeconds() % 60;

        totalDurationValue.setText(getString(
                R.string.stats_total_duration_value_format,
                totalMinutes,
                totalSeconds));
        averageDurationValue.setText(getString(
                R.string.stats_average_duration_value_format,
                averageMinutes,
                averageSeconds));
        totalCaloriesValue.setText(getString(
                R.string.stats_total_calories_value_format,
                summary.getTotalEstimatedCalories()));
        averageCaloriesValue.setText(getString(
                R.string.stats_average_calories_value_format,
                summary.getAverageEstimatedCalories()));
    }

    private void refreshTrendSection() {
        List<WeeklyTrendPoint> trendPoints = statisticsUseCase.getWeeklyTrendSeries(selectedTimeRange);
        if (trendPoints == null || trendPoints.isEmpty()) {
            trendValue.setText(R.string.stats_trend_empty);
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < trendPoints.size(); i++) {
            WeeklyTrendPoint point = trendPoints.get(i);
            int avgMinutes = point.getAverageDurationSeconds() / 60;
            int avgSeconds = point.getAverageDurationSeconds() % 60;
            String weekLabel = point.getWeekOffsetFromCurrent() == 0
                    ? getString(R.string.stats_trend_week_current)
                    : getString(R.string.stats_trend_week_ago_format, point.getWeekOffsetFromCurrent());

            builder.append(getString(
                    R.string.stats_trend_line_format,
                    weekLabel,
                    point.getSessionCount(),
                    avgMinutes,
                    avgSeconds));

            if (i < trendPoints.size() - 1) {
                builder.append('\n');
            }
        }
        trendValue.setText(builder.toString());
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
                record.getSetsCompleted(),
                record.getSetsPlanned());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.session_history_detail_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}