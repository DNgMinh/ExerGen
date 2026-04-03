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
import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.business.usecase.WorkoutUseCase;
import java.util.ArrayList;
import java.util.List;

// Fragment responsible for displaying the user's saved workouts,
// handles fetching data from the business layer and updating the UI
public class WorkoutsFragment extends Fragment implements WorkoutDetailFragment.WorkoutActionListener {

    private WorkoutUseCase workoutUseCase;
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    public void setDependencies(
            WorkoutUseCase workoutUseCase,
            ExerciseUseCase exerciseUseCase,
            SessionHistoryUseCase sessionHistoryUseCase,
            CaloriesEstimationUseCase caloriesEstimationUseCase) {
        this.workoutUseCase = workoutUseCase;
        this.exerciseUseCase = exerciseUseCase;
        this.sessionHistoryUseCase = sessionHistoryUseCase;
        this.caloriesEstimationUseCase = caloriesEstimationUseCase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (workoutUseCase == null
                || exerciseUseCase == null
                || sessionHistoryUseCase == null
                || caloriesEstimationUseCase == null) {
            throw new IllegalStateException("WorkoutsFragment dependencies not provided");
        }

        recyclerView = view.findViewById(R.id.exercise_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);
        emptyStateText.setText(getString(R.string.workouts_empty));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshWorkoutList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // Refresh the list whenever the fragment becomes visible again
        if (!hidden && isResumed()) {
            refreshWorkoutList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshWorkoutList();
    }

    private void refreshWorkoutList() {
        if (workoutUseCase == null) return;
        List<Workout> workouts = workoutUseCase.getAllWorkouts();

        if (workouts == null || workouts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setAdapter(new WorkoutAdapter(buildWorkoutItems(workouts),
                this::showWorkoutDetails, 
                null, // Disabled long-press to delete as requested
                this::startLiveWorkout,
                this::openWorkoutEditor));
        }
    }

    private List<WorkoutListItem> buildWorkoutItems(List<Workout> workouts) {
        List<WorkoutListItem> items = new ArrayList<>();
        for (Workout workout : workouts) {
            String details = getString(
                    R.string.workout_details_exercises_only_format,
                    workout.getSteps().size());
            items.add(new WorkoutListItem(workout, workout.getName(), details));
        }
        return items;
    }

    private void showWorkoutDetails(Workout workout) {
        if (workout == null) {
            return;
        }

        WorkoutDetailFragment detailFragment = WorkoutDetailFragment.newInstance(workout.getId());
        detailFragment.setDependencies(workoutUseCase, this);
        detailFragment.show(getParentFragmentManager(), "workout_detail");
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
        fragment.setDependencies(workoutUseCase, exerciseUseCase, sessionHistoryUseCase, caloriesEstimationUseCase);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openWorkoutEditor(Workout workout) {
        WorkoutEditorFragment fragment = WorkoutEditorFragment.newInstance(workout.getId());
        fragment.setDependencies(workoutUseCase, exerciseUseCase);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEditWorkout(Workout workout) {
        openWorkoutEditor(workout);
    }

    @Override
    public void onDeleteWorkout(Workout workout) {
        workoutUseCase.deleteWorkout(workout.getId());
        refreshWorkoutList();
    }
}
