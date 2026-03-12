package com.example.exergen.persistence;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.business.usecase.StatisticsSummary;
import com.example.exergen.business.usecase.StatisticsTimeRange;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.business.usecase.WeeklyTrendPoint;
import com.example.exergen.model.SessionRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, manifest = Config.NONE)
public class StatisticsDashboardPersistenceIntegrationTest {
    private SessionHistoryRepositorySQLite repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        repository = new SessionHistoryRepositorySQLite(context);

        DatabaseHelper helper = new DatabaseHelper(context);
        helper.getWritableDatabase().delete(DatabaseHelper.TABLE_SESSION_HISTORY, null, null);
    }

    @Test
    public void overallSummaryUsesPersistedSessionsFromSQLite() {
        repository.saveSession(createRecord("s-1", 1_700_000_000_000L, 600));
        repository.saveSession(createRecord("s-2", 1_700_000_000_100L, 300));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        StatisticsSummary summary = useCase.getOverallSummary();

        assertEquals(2, summary.getTotalSessions());
        assertEquals(900, summary.getCumulativeDurationSeconds());
        assertEquals(450, summary.getAverageSessionLengthSeconds());
        assertEquals(120, summary.getTotalEstimatedCalories());
        assertEquals(60, summary.getAverageEstimatedCalories());
    }

    @Test
    public void timeRangeAndTrendAreComputedFromPersistedSessions() {
        long now = System.currentTimeMillis();
        repository.saveSession(createRecord("recent", now - toMs(2), 600));
        repository.saveSession(createRecord("mid", now - toMs(20), 1_200));
        repository.saveSession(createRecord("old", now - toMs(50), 300));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);

        StatisticsSummary lastThirtyDays = useCase.getSummaryForTimeRange(StatisticsTimeRange.LAST_30_DAYS);
        assertEquals(2, lastThirtyDays.getTotalSessions());
        assertEquals(1_800, lastThirtyDays.getCumulativeDurationSeconds());

        List<WeeklyTrendPoint> trend = useCase.getWeeklyTrendSeries(StatisticsTimeRange.LAST_30_DAYS);
        assertEquals(5, trend.size());

        // Order is oldest to newest: offsets 4,3,2,1,0
        assertEquals(4, trend.get(0).getWeekOffsetFromCurrent());
        assertEquals(0, trend.get(0).getSessionCount());

        assertEquals(2, trend.get(2).getWeekOffsetFromCurrent());
        assertEquals(1, trend.get(2).getSessionCount());
        assertEquals(1_200, trend.get(2).getAverageDurationSeconds());

        assertEquals(0, trend.get(4).getWeekOffsetFromCurrent());
        assertEquals(1, trend.get(4).getSessionCount());
        assertEquals(600, trend.get(4).getAverageDurationSeconds());
    }

    private static long toMs(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }

    private static SessionRecord createRecord(String id, long completedAtEpochMs, int durationSeconds) {
        return new SessionRecord(
                id,
                "workout-1",
                "Workout",
                completedAtEpochMs,
                durationSeconds,
                5,
                3,
                3);
    }
}
