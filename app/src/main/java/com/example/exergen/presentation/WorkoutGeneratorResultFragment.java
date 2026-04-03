package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.exergen.R;
import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Workout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WorkoutGeneratorResultFragment extends BottomSheetDialogFragment {

    public interface ResultListener {
        void onRegenerate();
        void onManualEdit(Workout workout);
        void onStartNow(Workout workout);
        void onAdjustSettings();
    }

    private Workout workout;
    private String summaryText;
    private String previewText;
    private ResultListener listener;
    
    private WorkoutUseCase workoutUseCase;
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;

    public static WorkoutGeneratorResultFragment newInstance(Workout workout, String summary, String preview) {
        WorkoutGeneratorResultFragment fragment = new WorkoutGeneratorResultFragment();
        fragment.workout = workout;
        fragment.summaryText = summary;
        fragment.previewText = preview;
        return fragment;
    }

    public void setDependencies(WorkoutUseCase workoutUseCase,
                               ExerciseUseCase exerciseUseCase,
                               SessionHistoryUseCase sessionHistoryUseCase,
                               CaloriesEstimationUseCase caloriesEstimationUseCase,
                               ResultListener listener) {
        this.workoutUseCase = workoutUseCase;
        this.exerciseUseCase = exerciseUseCase;
        this.sessionHistoryUseCase = sessionHistoryUseCase;
        this.caloriesEstimationUseCase = caloriesEstimationUseCase;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_generator_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etName = view.findViewById(R.id.et_generated_workout_name);
        TextView tvSummary = view.findViewById(R.id.tv_builder_summary);
        TextView tvPreview = view.findViewById(R.id.tv_builder_preview);
        
        Button btnSave = view.findViewById(R.id.btn_save_workout);
        Button btnStart = view.findViewById(R.id.btn_start_workout);
        Button btnRegen = view.findViewById(R.id.btn_regenerate_workout);
        Button btnManual = view.findViewById(R.id.btn_manual_edit);
        Button btnAdjust = view.findViewById(R.id.btn_edit_constraints);

        if (workout != null) {
            etName.setText(workout.getName());
        }
        tvSummary.setText(summaryText);
        tvPreview.setText(previewText);

        btnSave.setOnClickListener(v -> {
            if (saveWorkout(etName.getText().toString())) {
                Toast.makeText(getContext(), R.string.workout_builder_saved_message, Toast.LENGTH_SHORT).show();
            }
        });

        btnStart.setOnClickListener(v -> {
            if (saveWorkout(etName.getText().toString())) {
                listener.onStartNow(workout);
                dismiss();
            }
        });

        btnRegen.setOnClickListener(v -> {
            listener.onRegenerate();
            dismiss();
        });

        btnManual.setOnClickListener(v -> {
            if (saveWorkout(etName.getText().toString())) {
                listener.onManualEdit(workout);
                dismiss();
            }
        });

        btnAdjust.setOnClickListener(v -> {
            listener.onAdjustSettings();
            dismiss();
        });
    }

    private boolean saveWorkout(String name) {
        if (workout == null) return false;
        String trimmedName = name.trim();
        if (TextUtils.isEmpty(trimmedName)) {
            Toast.makeText(getContext(), "Please enter a name for the workout.", Toast.LENGTH_SHORT).show();
            return false;
        }

        workout = new Workout(
                workout.getId(),
                trimmedName,
                workout.getSets(),
                workout.getSteps(),
                workout.getCreatedAtMs());

        workoutUseCase.saveWorkout(workout);
        return true;
    }
}
