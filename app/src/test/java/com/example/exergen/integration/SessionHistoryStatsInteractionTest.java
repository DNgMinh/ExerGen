package com.example.exergen.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Build;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.presentation.StatsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = { Build.VERSION_CODES.O_MR1 })
public class SessionHistoryStatsInteractionTest {

    @Test
    public void emptyHistoryShowsEmptyState() {
        StatsFragment fragment = launchStatsFragment(new InMemorySessionHistoryRepository());
        View root = fragment.requireView();

        RecyclerView recyclerView = root.findViewById(R.id.session_history_recycler_view);
        View emptyText = root.findViewById(R.id.session_history_empty_text);

        assertEquals(View.GONE, recyclerView.getVisibility());
        assertEquals(View.VISIBLE, emptyText.getVisibility());
    }

    @Test
    public void populatedHistoryShowsListAndOpensDetailDialog() {
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        repository.saveSession(createRecord("s-1", "Session One", 1700000000000L));
        repository.saveSession(createRecord("s-2", "Session Two", 1700000001000L));

        StatsFragment fragment = launchStatsFragment(repository);
        View root = fragment.requireView();

        RecyclerView recyclerView = root.findViewById(R.id.session_history_recycler_view);
        View emptyText = root.findViewById(R.id.session_history_empty_text);

        assertEquals(View.VISIBLE, recyclerView.getVisibility());
        assertEquals(View.GONE, emptyText.getVisibility());
        assertNotNull(recyclerView.getAdapter());
        assertEquals(2, recyclerView.getAdapter().getItemCount());

        RecyclerView.Adapter rawAdapter = recyclerView.getAdapter();
        RecyclerView.ViewHolder holder = rawAdapter.onCreateViewHolder(recyclerView, 0);
        rawAdapter.onBindViewHolder(holder, 0);
        holder.itemView.performClick();
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();

        android.app.Dialog dialog = ShadowDialog.getLatestDialog();
        assertNotNull(dialog);
        assertTrue(dialog.isShowing());
    }

    private static StatsFragment launchStatsFragment(ISessionHistoryRepository repository) {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).setup().get();
        SessionHistoryUseCase useCase = new SessionHistoryUseCase(repository);
        StatisticsUseCase statisticsUseCase = new StatisticsUseCase(repository);

        StatsFragment fragment = new StatsFragment();
        fragment.setSessionHistoryUseCaseForTesting(useCase);
        fragment.setStatisticsUseCaseForTesting(statisticsUseCase);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow();

        return fragment;
    }

    private static SessionRecord createRecord(String id, String workoutName, long completedAtMs) {
        return new SessionRecord(
                id,
                "manual-timer",
                workoutName,
                completedAtMs,
                120,
                1,
                3,
                3);
    }

    private static class InMemorySessionHistoryRepository implements ISessionHistoryRepository {
        private final List<SessionRecord> records = new ArrayList<>();

        @Override
        public void saveSession(SessionRecord sessionRecord) {
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).getId().equals(sessionRecord.getId())) {
                    records.set(i, sessionRecord);
                    return;
                }
            }
            records.add(sessionRecord);
        }

        @Override
        public SessionRecord getSessionById(String sessionId) {
            for (SessionRecord record : records) {
                if (record.getId().equals(sessionId)) {
                    return record;
                }
            }
            return null;
        }

        @Override
        public List<SessionRecord> getAllSessions() {
            return new ArrayList<>(records);
        }
    }
}
