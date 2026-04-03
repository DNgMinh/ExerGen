package com.example.exergen.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.WorkoutStep;

import java.util.List;

public class WorkoutStepAdapter extends RecyclerView.Adapter<WorkoutStepAdapter.ViewHolder> {
    private final List<WorkoutStep> steps;
    private final ExerciseUseCase exerciseUseCase;
    private final StepListener listener;

    public interface StepListener {
        void onRemove(int position);
        void onStepUpdated(int position, WorkoutStep updatedStep);
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public WorkoutStepAdapter(List<WorkoutStep> steps, ExerciseUseCase exerciseUseCase, StepListener listener) {
        this.steps = steps;
        this.exerciseUseCase = exerciseUseCase;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_editor_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutStep step = steps.get(position);
        Exercise ex = exerciseUseCase.getExerciseById(step.getExerciseId());
        holder.tvName.setText(ex != null ? ex.getName() : "Unknown");

        holder.tvWork.setText("Work: " + step.getWorkSeconds() + "s");
        holder.tvRest.setText("Rest: " + step.getRestSeconds() + "s");

        holder.tvWork.setOnClickListener(v -> showEditDialog(v.getContext(), holder.getAdapterPosition(), true));
        holder.tvRest.setOnClickListener(v -> showEditDialog(v.getContext(), holder.getAdapterPosition(), false));

        holder.btnRemove.setOnClickListener(v -> listener.onRemove(holder.getAdapterPosition()));

        // Enable long-press to drag on the entire item
        holder.itemView.setOnLongClickListener(v -> {
            listener.onStartDrag(holder);
            return true;
        });
    }

    private void showEditDialog(Context context, int position, boolean isWork) {
        WorkoutStep currentStep = steps.get(position);
        String title = isWork ? "Edit Work Time (s)" : "Edit Rest Time (s)";
        int currentValue = isWork ? currentStep.getWorkSeconds() : currentStep.getRestSeconds();

        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(currentValue));
        input.setSelection(input.getText().length());

        FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = context.getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_dialog_padding_material);
        params.leftMargin = margin;
        params.rightMargin = margin;
        input.setLayoutParams(params);
        container.addView(input);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(container)
                .setPositiveButton("OK", (dialog, which) -> {
                    try {
                        String text = input.getText().toString();
                        if (!text.isEmpty()) {
                            int newValue = Integer.parseInt(text);
                            
                            if (isWork && newValue < 1) {
                                Toast.makeText(context, "Work time must be at least 1s", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!isWork && newValue < 0) {
                                Toast.makeText(context, "Rest time cannot be negative", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            WorkoutStep updated;
                            if (isWork) {
                                updated = new WorkoutStep(currentStep.getExerciseId(), newValue, currentStep.getRestSeconds());
                            } else {
                                updated = new WorkoutStep(currentStep.getExerciseId(), currentStep.getWorkSeconds(), newValue);
                            }
                            listener.onStepUpdated(position, updated);
                            notifyItemChanged(position);
                        }
                    } catch (NumberFormatException ignored) {}
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvWork, tvRest;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_step_name);
            tvWork = itemView.findViewById(R.id.tv_step_work);
            tvRest = itemView.findViewById(R.id.tv_step_rest);
            btnRemove = itemView.findViewById(R.id.btn_remove_step);
        }
    }
}
