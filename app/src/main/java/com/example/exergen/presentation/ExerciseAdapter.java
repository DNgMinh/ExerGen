package com.example.exergen.presentation;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import com.example.exergen.model.Exercise;
import java.util.List;

// RecyclerView adapter for displaying a list of exercise items, binds
// exercise domain models to the UI views
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private OnExerciseClickListener clickListener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener clickListener) {
        this.exercises = exercises;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.name.setText(exercise.getName());

        Context context = holder.itemView.getContext();
        String muscles = TextUtils.join(", ", exercise.getMuscleGroups());
        String equipment = TextUtils.join(", ", exercise.getEquipment());

        String attributes = context.getString(R.string.exercise_attributes_format, muscles, equipment);
        holder.attributes.setText(attributes);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExerciseClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView attributes;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exercise_name);
            attributes = itemView.findViewById(R.id.exercise_attributes);
        }
    }
}