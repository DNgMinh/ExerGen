package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.model.Workout;
import com.example.exergen.business.usecase.WorkoutUseCase;
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
        emptyStateText.setText("No workouts found");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch the list of workouts from the business layer
        List<Workout> workouts = workoutUseCase.getAllWorkouts();

        // Toggle visibility based on whether data exists
        if (workouts == null || workouts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setAdapter(new WorkoutAdapter(workouts));
        }
    }
}