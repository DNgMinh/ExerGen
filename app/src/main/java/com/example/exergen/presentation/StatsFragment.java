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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.StatisticsSummary;
import com.example.exergen.business.usecase.StatisticsTimeRange;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.model.WeeklyTrendPoint;
import com.example.exergen.model.SessionRecord;

import java.text.DateFormat;
import java.util.ArrayList;
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
    private TextView trendValue;
    private Spinner timeRangeSpinner;
    private StatisticsTimeRange selectedTimeRange = StatisticsTimeRange.ALL_TIME;

    public void setDependencies(SessionHistoryUseCase sessionHistoryUseCase, StatisticsUseCase statisticsUseCase) {
        this.sessionHistoryUseCase = sessionHistoryUseCase;
        this.statisticsUseCase = statisticsUseCase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sessionHistoryUseCase == null || statisticsUseCase == null) {
            throw new IllegalStateException("StatsFragment dependencies not provided");
        }

        sessionHistoryRecyclerView = view.findViewById(R.id.session_history_recycler_view);
        emptyStateText = view.findViewById(R.id.session_history_empty_text);
        totalSessionsValue = view.findViewById(R.id.stats_total_sessions_value);
        totalDurationValue = view.findViewById(R.id.stats_total_duration_value);
        averageDurationValue = view.findViewById(R.id.stats_average_duration_value);
        totalCaloriesValue = view.findViewById(R.id.stats_total_calories_value);
        trendValue = view.findViewById(R.id.stats_trend_value);
        timeRangeSpinner = view.findViewById(R.id.stats_time_range_spinner);
        
        sessionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionHistoryRecyclerView.setNestedScrollingEnabled(false);

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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // Refresh when navigating back to this tab
        if (!hidden && isResumed()) {
            refreshStatisticsSection();
            refreshSessionHistory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStatisticsSection();
        refreshSessionHistory();
    }

    private void refreshStatisticsSection() {
        if (statisticsUseCase == null) return;
        refreshOverallSummary();
        refreshTrendSection();
    }

    private void refreshOverallSummary() {
        StatisticsSummary summary = statisticsUseCase.getSummaryForTimeRange(selectedTimeRange);
        
        totalSessionsValue.setText(getString(R.string.stats_value_sessions_format, summary.getTotalSessions()));
        
        int totalMinutes = summary.getCumulativeDurationSeconds() / 60;
        int totalSeconds = summary.getCumulativeDurationSeconds() % 60;
        totalDurationValue.setText(getString(R.string.stats_value_duration_format, totalMinutes, totalSeconds));
        
        int averageMinutes = summary.getAverageSessionLengthSeconds() / 60;
        int averageSeconds = summary.getAverageSessionLengthSeconds() % 60;
        averageDurationValue.setText(getString(R.string.stats_value_duration_format, averageMinutes, averageSeconds));
        
        totalCaloriesValue.setText(getString(R.string.stats_value_calories_format, summary.getTotalEstimatedCalories()));
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
        if (sessionHistoryUseCase == null) return;
        List<SessionRecord> sessions = sessionHistoryUseCase.getSessionHistory();
        if (sessions == null || sessions.isEmpty()) {
            sessionHistoryRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            return;
        }

        emptyStateText.setVisibility(View.GONE);
        sessionHistoryRecyclerView.setVisibility(View.VISIBLE);
        sessionHistoryRecyclerView.setAdapter(new SessionHistoryAdapter(buildSessionItems(sessions), this::showSessionDetails));
    }

    private List<SessionHistoryListItem> buildSessionItems(List<SessionRecord> sessions) {
        List<SessionHistoryListItem> items = new ArrayList<>();
        for (SessionRecord session : sessions) {
            String completedAtText = DateFormat.getDateTimeInstance().format(new Date(session.getCompletedAtEpochMs()));
            String summary;
            if (session.hasEstimatedCalories()) {
                summary = getString(
                        R.string.session_history_item_summary_with_calories_format,
                        completedAtText,
                        session.getTotalDurationSeconds(),
                        session.getSetsCompleted(),
                        session.getSetsPlanned(),
                        session.getEstimatedCalories());
            } else {
                summary = getString(
                        R.string.session_history_item_summary_without_calories_format,
                        completedAtText,
                        session.getTotalDurationSeconds(),
                        session.getSetsCompleted(),
                        session.getSetsPlanned(),
                        getString(R.string.session_history_calories_unavailable));
            }
            items.add(new SessionHistoryListItem(session.getId(), session.getWorkoutName(), summary));
        }
        return items;
    }

    private void showSessionDetails(String sessionId) {
        if (sessionId == null) {
            return;
        }

        SessionDetailFragment detailFragment = SessionDetailFragment.newInstance(sessionId);
        detailFragment.setDependencies(sessionHistoryUseCase);
        detailFragment.show(getParentFragmentManager(), "session_detail");
    }
}
