package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.example.exergen.R;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.stream.Collectors;

public class WorkoutDetailFragment extends BottomSheetDialogFragment {

    private static final String ARG_WORKOUT_ID = "workout_id";

    public interface WorkoutActionListener {
        void onEditWorkout(Workout workout);
        void onDeleteWorkout(Workout workout);
    }

    private WorkoutUseCase workoutUseCase;
    private WorkoutActionListener actionListener;

    public static WorkoutDetailFragment newInstance(String workoutId) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORKOUT_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDependencies(WorkoutUseCase workoutUseCase, WorkoutActionListener listener) {
        this.workoutUseCase = workoutUseCase;
        this.actionListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String workoutId = getArguments() != null ? getArguments().getString(ARG_WORKOUT_ID) : null;
        if (workoutId == null || workoutUseCase == null) {
            dismiss();
            return;
        }

        Workout workout = workoutUseCase.getWorkoutById(workoutId);
        if (workout == null) {
            dismiss();
            return;
        }

        TextView nameText = view.findViewById(R.id.workout_detail_name);
        TextView setsText = view.findViewById(R.id.workout_detail_sets);
        TextView countText = view.findViewById(R.id.workout_detail_exercise_count);
        TextView durationText = view.findViewById(R.id.workout_detail_duration);
        TextView exerciseListText = view.findViewById(R.id.workout_detail_exercise_list);
        
        ImageButton deleteButton = view.findViewById(R.id.btn_workout_detail_delete);
        Button editButton = view.findViewById(R.id.btn_workout_detail_edit);
        Button closeButton = view.findViewById(R.id.btn_workout_detail_close);

        nameText.setText(workout.getName());
        setsText.setText(String.valueOf(workout.getSets()));
        countText.setText(String.valueOf(workout.getSteps().size()));
        
        int totalSeconds = workoutUseCase.getTotalDurationSeconds(workout);
        durationText.setText((totalSeconds / 60) + "m");

        List<Exercise> exercises = workoutUseCase.getExercisesForWorkout(workout);
        String exerciseNames = exercises.stream()
                .map(e -> "• " + e.getName())
                .collect(Collectors.joining("\n"));
        
        if (exerciseNames.isEmpty()) {
            exerciseNames = getString(R.string.workout_detail_none);
        }
        exerciseListText.setText(exerciseNames);

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.workout_delete_title)
                    .setMessage(getString(R.string.workout_delete_message, workout.getName()))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.workout_delete_action, (dialog, which) -> {
                        if (actionListener != null) {
                            actionListener.onDeleteWorkout(workout);
                        }
                        dismiss();
                    })
                    .show();
        });

        editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditWorkout(workout);
            }
            dismiss();
        });

        closeButton.setOnClickListener(v -> dismiss());
    }
}
