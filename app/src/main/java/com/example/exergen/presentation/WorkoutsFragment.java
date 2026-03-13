package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.WorkoutMetricsService;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.business.usecase.WorkoutUseCase;
import java.util.ArrayList;
import java.util.List;

// Fragment responsible for displaying the user's saved workouts,
// handles fetching data from the business layer and updating the UI
public class WorkoutsFragment extends Fragment {

    private WorkoutUseCase workoutUseCase;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutUseCase = AppBootstrap.get().workoutUseCase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.exercise_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);
        emptyStateText.setText(getString(R.string.workouts_empty));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshWorkoutList();
    }

    private void refreshWorkoutList() {
        List<Workout> workouts = workoutUseCase.getAllWorkouts();

        if (workouts == null || workouts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setAdapter(new WorkoutAdapter(workouts, 
                this::showWorkoutDetails, 
                this::confirmDeleteWorkout,
                this::startLiveWorkout));
        }
    }

    private void showWorkoutDetails(Workout workout) {
        if (workout == null || getContext() == null) {
            return;
        }

        List<Exercise> exercises = workoutUseCase.getExercisesForWorkout(workout);
        List<String> names = new ArrayList<>();
        for (Exercise exercise : exercises) {
            names.add(exercise.getName());
        }
        if (names.isEmpty()) {
            names.add(getString(R.string.workout_detail_none));
        }

        int totalSeconds = WorkoutMetricsService.calculateTotalDurationSeconds(workout);
        String detailText = getString(
                R.string.workout_detail_body_format,
                workout.getName(),
                workout.getRounds(),
                workout.getExerciseIds().size(),
                totalSeconds,
                String.join(", ", names));

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.workout_detail_title)
                .setMessage(detailText)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void confirmDeleteWorkout(Workout workout) {
        if (workout == null || getContext() == null) {
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.workout_delete_title)
                .setMessage(getString(R.string.workout_delete_message, workout.getName()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.workout_delete_action, (dialog, which) -> {
                    workoutUseCase.deleteWorkout(workout.getId());
                    refreshWorkoutList();
                })
                .show();
    }

    private void startLiveWorkout(Workout workout) {
        if (workout == null) return;
        
        LiveWorkoutFragment fragment = LiveWorkoutFragment.newInstance(workout.getId());
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}