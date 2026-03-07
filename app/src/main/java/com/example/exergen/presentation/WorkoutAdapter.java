package com.example.exergen.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import com.example.exergen.business.service.WorkoutMetricsService;
import com.example.exergen.model.Workout;
import java.util.List;

// RecyclerView adapter for displaying a list of workout items,
// binds workout domain models to the UI views
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workouts;

    // Initializes the adapter with a list of workouts
    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.name.setText(workout.getName());

        int totalDuration = WorkoutMetricsService.calculateTotalDurationSeconds(workout);
        Context context = holder.itemView.getContext();
        String details = context.getString(R.string.workout_details_format,
                workout.getExerciseIds().size(),
                totalDuration / 60);

        holder.details.setText(details);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView details;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.workout_name);
            details = itemView.findViewById(R.id.workout_details);
        }
    }
}