package com.example.exergen.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import java.util.List;

public class SessionHistoryAdapter extends RecyclerView.Adapter<SessionHistoryAdapter.SessionViewHolder> {
    public interface OnSessionClickListener {
        void onSessionClick(String sessionId);
    }

    private final List<SessionHistoryListItem> sessions;
    private final OnSessionClickListener onSessionClickListener;

    public SessionHistoryAdapter(List<SessionHistoryListItem> sessions, OnSessionClickListener onSessionClickListener) {
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
        SessionHistoryListItem session = sessions.get(position);
        holder.workoutName.setText(session.getWorkoutName());
        holder.summary.setText(session.getSummary());

        holder.itemView.setOnClickListener(v -> {
            if (onSessionClickListener != null) {
                onSessionClickListener.onSessionClick(session.getSessionId());
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
