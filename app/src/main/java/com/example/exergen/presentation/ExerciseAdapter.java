package com.example.exergen.presentation;

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

    private List<ExerciseListItem> exerciseItems;
    private OnExerciseClickListener clickListener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public ExerciseAdapter(List<ExerciseListItem> exerciseItems, OnExerciseClickListener clickListener) {
        this.exerciseItems = exerciseItems;
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
        ExerciseListItem item = exerciseItems.get(position);
        holder.name.setText(item.getName());
        holder.attributes.setText(item.getAttributes());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExerciseClick(item.getExercise());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseItems.size();
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