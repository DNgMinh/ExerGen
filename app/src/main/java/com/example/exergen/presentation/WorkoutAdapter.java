package com.example.exergen.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import java.util.List;

// RecyclerView adapter for displaying a list of workout items,
// binds workout domain models to the UI views
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private final List<WorkoutListItem> workoutItems;
    private final OnWorkoutClickListener clickListener;
    private final OnWorkoutLongClickListener longClickListener;
    private final OnWorkoutPlayClickListener playClickListener;
    private final OnWorkoutEditClickListener editClickListener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(com.example.exergen.model.Workout workout);
    }

    public interface OnWorkoutLongClickListener {
        void onWorkoutLongClick(com.example.exergen.model.Workout workout);
    }

    public interface OnWorkoutPlayClickListener {
        void onWorkoutPlayClick(com.example.exergen.model.Workout workout);
    }

    public interface OnWorkoutEditClickListener {
        void onWorkoutEditClick(com.example.exergen.model.Workout workout);
    }

    // Initializes the adapter with a list of workouts
    public WorkoutAdapter(List<WorkoutListItem> workoutItems,
            OnWorkoutClickListener clickListener,
            OnWorkoutLongClickListener longClickListener,
            OnWorkoutPlayClickListener playClickListener,
            OnWorkoutEditClickListener editClickListener) {
        this.workoutItems = workoutItems;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.playClickListener = playClickListener;
        this.editClickListener = editClickListener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutListItem item = workoutItems.get(position);
        holder.name.setText(item.getName());
        holder.details.setText(item.getDetails());
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onWorkoutClick(item.getWorkout());
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onWorkoutLongClick(item.getWorkout());
                return true;
            }
            return false;
        });
        holder.btnPlay.setOnClickListener(v -> {
            if (playClickListener != null) {
                playClickListener.onWorkoutPlayClick(item.getWorkout());
            }
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onWorkoutEditClick(item.getWorkout());
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutItems.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView details;
        ImageButton btnPlay;
        ImageButton btnEdit;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.workout_name);
            details = itemView.findViewById(R.id.workout_details);
            btnPlay = itemView.findViewById(R.id.btn_play_workout);
            btnEdit = itemView.findViewById(R.id.btn_edit_workout);
        }
    }
}
