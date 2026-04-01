package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WorkoutEditorFragment extends Fragment {
    private static final String ARG_WORKOUT_ID = "workout_id";

    private String workoutId;
    private WorkoutUseCase workoutUseCase;
    private ExerciseUseCase exerciseUseCase;

    private EditText etWorkoutName;
    private EditText etSets;
    private RecyclerView rvSteps;
    private Button btnAddExercise;
    private Button btnSave;
    private ImageButton btnCancel;

    private List<WorkoutStep> steps = new ArrayList<>();
    private WorkoutStepAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private long originalCreatedAtMs = -1;

    public static WorkoutEditorFragment newInstance(@Nullable String workoutId) {
        WorkoutEditorFragment fragment = new WorkoutEditorFragment();
        if (workoutId != null) {
            Bundle args = new Bundle();
            args.putString(ARG_WORKOUT_ID, workoutId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public void setDependencies(WorkoutUseCase workoutUseCase, ExerciseUseCase exerciseUseCase) {
        this.workoutUseCase = workoutUseCase;
        this.exerciseUseCase = exerciseUseCase;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workoutId = getArguments().getString(ARG_WORKOUT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (workoutUseCase == null || exerciseUseCase == null) {
            throw new IllegalStateException("WorkoutEditorFragment dependencies not provided");
        }

        etWorkoutName = view.findViewById(R.id.et_edit_workout_name);
        etSets = view.findViewById(R.id.et_edit_sets);
        rvSteps = view.findViewById(R.id.rv_edit_steps);
        btnAddExercise = view.findViewById(R.id.btn_editor_add_exercise);
        btnSave = view.findViewById(R.id.btn_editor_save);
        btnCancel = view.findViewById(R.id.btn_editor_cancel);

        setupRecyclerView();

        if (workoutId != null) {
            loadWorkout();
        } else {
            etWorkoutName.setText("New Workout");
            etSets.setText("3");
            originalCreatedAtMs = System.currentTimeMillis();
        }

        btnAddExercise.setOnClickListener(v -> openExercisePicker());
        btnSave.setOnClickListener(v -> saveWorkout());
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void setupRecyclerView() {
        adapter = new WorkoutStepAdapter(steps, exerciseUseCase, new WorkoutStepAdapter.StepListener() {
            @Override
            public void onRemove(int position) {
                steps.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onStepUpdated(int position, WorkoutStep updatedStep) {
                steps.set(position, updatedStep);
            }

            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
        rvSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSteps.setAdapter(adapter);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                
                Collections.swap(steps, fromPos, toPos);
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
        });
        itemTouchHelper.attachToRecyclerView(rvSteps);
    }

    private void loadWorkout() {
        Workout workout = workoutUseCase.getWorkoutById(workoutId);
        if (workout != null) {
            etWorkoutName.setText(workout.getName());
            etSets.setText(String.valueOf(workout.getSets()));
            originalCreatedAtMs = workout.getCreatedAtMs();
            steps.clear();
            steps.addAll(workout.getSteps());
            adapter.notifyDataSetChanged();
        }
    }

    private void openExercisePicker() {
        ExercisePickerFragment picker = new ExercisePickerFragment();
        picker.setDependencies(exerciseUseCase);
        picker.setOnExerciseSelectedListener(this::addExercise);
        picker.show(getParentFragmentManager(), "exercise_picker");
    }

    private void addExercise(Exercise exercise) {
        steps.add(new WorkoutStep(exercise.getId(), 45, 15));
        adapter.notifyItemInserted(steps.size() - 1);
    }

    private void saveWorkout() {
        String name = etWorkoutName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Name required", Toast.LENGTH_SHORT).show();
            return;
        }

        String setsText = etSets.getText().toString().trim();
        int setsCount;
        try {
            setsCount = Integer.parseInt(setsText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid sets", Toast.LENGTH_SHORT).show();
            return;
        }

        if (steps.isEmpty()) {
            Toast.makeText(getContext(), "Add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = (workoutId != null) ? workoutId : "custom-" + UUID.randomUUID().toString();
        
        // Preserve original creation time
        Workout workout = new Workout(id, name, setsCount, steps, originalCreatedAtMs);
        workoutUseCase.saveWorkout(workout);

        Toast.makeText(getContext(), "Workout saved", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }
}
