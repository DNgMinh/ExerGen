package com.example.exergen.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.model.SessionRecord;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class SessionHistoryAdapter extends RecyclerView.Adapter<SessionHistoryAdapter.SessionViewHolder> {
    public interface OnSessionClickListener {
        void onSessionClick(SessionRecord sessionRecord);
    }

    private final List<SessionRecord> sessions;
    private final OnSessionClickListener onSessionClickListener;

    public SessionHistoryAdapter(List<SessionRecord> sessions, OnSessionClickListener onSessionClickListener) {
        this.sessions = sessions;
        this.onSessionClickListener = onSessionClickListener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_session_history, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionRecord session = sessions.get(position);
        holder.workoutName.setText(session.getWorkoutName());

        String completedAtText = DateFormat.getDateTimeInstance().format(new Date(session.getCompletedAtEpochMs()));
        String summary = holder.itemView.getContext().getString(
                R.string.session_history_item_summary_format,
                completedAtText,
                session.getTotalDurationSeconds(),
                session.getRoundsCompleted(),
                session.getRoundsPlanned());
        holder.summary.setText(summary);

        holder.itemView.setOnClickListener(v -> {
            if (onSessionClickListener != null) {
                onSessionClickListener.onSessionClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private final TextView workoutName;
        private final TextView summary;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.session_workout_name);
            summary = itemView.findViewById(R.id.session_summary);
        }
    }
}
