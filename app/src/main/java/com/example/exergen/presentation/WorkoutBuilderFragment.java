package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.gridlayout.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkoutBuilderFragment extends Fragment implements WorkoutGeneratorResultFragment.ResultListener {

    private WorkoutBuilderUseCase workoutBuilderUseCase;
    private WorkoutUseCase workoutUseCase;
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;
    private IEnumMapper enumMapper;

    private RadioGroup rgBuildMode;
    private View containerModeCount;
    private View containerModeTime;
    private EditText etExerciseCount;
    private EditText etTargetTime;
    
    private Button btnIntensityToggle;
    private View containerIntensityDropdown;
    private RadioGroup rgIntensity;
    private View containerCustomIntervals;
    private EditText etCustomWork;
    private EditText etCustomRest;

    private Button btnMuscleToggle;
    private GridLayout containerMuscleDropdown;
    private Map<MuscleGroup, CheckBox> muscleCheckBoxes = new HashMap<>();

    private Button btnEquipmentToggle;
    private GridLayout containerEquipmentDropdown;
    private Map<EquipmentType, CheckBox> equipmentCheckBoxes = new HashMap<>();

    private Button btnGenerateWorkout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (workoutBuilderUseCase == null) {
            workoutBuilderUseCase = AppBootstrap.get().workoutBuilderUseCase;
            workoutUseCase = AppBootstrap.get().workoutUseCase;
            exerciseUseCase = AppBootstrap.get().exerciseUseCase;
            sessionHistoryUseCase = AppBootstrap.get().sessionHistoryUseCase;
            caloriesEstimationUseCase = AppBootstrap.get().caloriesEstimationUseCase;
            enumMapper = AppBootstrap.get().enumMapper;
        }
    }

    public void setDependencies(WorkoutBuilderUseCase workoutBuilderUseCase,
            WorkoutUseCase workoutUseCase,
            ExerciseUseCase exerciseUseCase,
            SessionHistoryUseCase sessionHistoryUseCase,
            CaloriesEstimationUseCase caloriesEstimationUseCase,
            IEnumMapper enumMapper) {
        this.workoutBuilderUseCase = workoutBuilderUseCase;
        this.workoutUseCase = workoutUseCase;
        this.exerciseUseCase = exerciseUseCase;
        this.sessionHistoryUseCase = sessionHistoryUseCase;
        this.caloriesEstimationUseCase = caloriesEstimationUseCase;
        this.enumMapper = enumMapper;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_builder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupDynamicOptions();
        setupListeners();
    }

    private void setupDynamicOptions() {
        containerMuscleDropdown.removeAllViews();
        muscleCheckBoxes.clear();
        for (MuscleGroup group : MuscleGroup.values()) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(group.getLabel());
            muscleCheckBoxes.put(group, cb);
            containerMuscleDropdown.addView(cb);
        }

        containerEquipmentDropdown.removeAllViews();
        equipmentCheckBoxes.clear();
        for (EquipmentType type : EquipmentType.values()) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(type.getLabel());
            equipmentCheckBoxes.put(type, cb);
            containerEquipmentDropdown.addView(cb);
        }
    }

    private void setupListeners() {
        rgBuildMode.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isTimeMode = checkedId == R.id.rb_mode_time;
            containerModeCount.setVisibility(isTimeMode ? View.GONE : View.VISIBLE);
            containerModeTime.setVisibility(isTimeMode ? View.VISIBLE : View.GONE);
        });

        btnIntensityToggle.setOnClickListener(v -> toggleVisibility(containerIntensityDropdown, btnIntensityToggle, "Intensity & Intervals"));
        btnMuscleToggle.setOnClickListener(v -> toggleVisibility(containerMuscleDropdown, btnMuscleToggle, "Muscle Groups"));
        btnEquipmentToggle.setOnClickListener(v -> toggleVisibility(containerEquipmentDropdown, btnEquipmentToggle, "Equipment"));

        rgIntensity.setOnCheckedChangeListener((group, checkedId) -> {
            containerCustomIntervals.setVisibility(checkedId == R.id.rb_intensity_custom ? View.VISIBLE : View.GONE);
        });

        btnGenerateWorkout.setOnClickListener(v -> generateWorkout());
    }

    private void toggleVisibility(View container, Button toggleButton, String label) {
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            toggleButton.setText("▼ " + label);
        } else {
            container.setVisibility(View.VISIBLE);
            toggleButton.setText("▲ " + label);
        }
    }

    private void bindViews(View view) {
        rgBuildMode = view.findViewById(R.id.rg_build_mode);
        containerModeCount = view.findViewById(R.id.container_mode_count);
        containerModeTime = view.findViewById(R.id.container_mode_time);
        etExerciseCount = view.findViewById(R.id.et_exercise_count);
        etTargetTime = view.findViewById(R.id.et_target_time);
        
        btnIntensityToggle = view.findViewById(R.id.btn_intensity_toggle);
        containerIntensityDropdown = view.findViewById(R.id.container_intensity_dropdown);
        rgIntensity = view.findViewById(R.id.rg_intensity);
        containerCustomIntervals = view.findViewById(R.id.container_custom_intervals);
        etCustomWork = view.findViewById(R.id.et_custom_work);
        etCustomRest = view.findViewById(R.id.et_custom_rest);

        btnMuscleToggle = view.findViewById(R.id.btn_muscle_toggle);
        containerMuscleDropdown = (GridLayout) view.findViewById(R.id.container_muscle_dropdown);

        btnEquipmentToggle = view.findViewById(R.id.btn_equipment_toggle);
        containerEquipmentDropdown = (GridLayout) view.findViewById(R.id.container_equipment_dropdown);

        btnGenerateWorkout = view.findViewById(R.id.btn_generate_workout);
    }

    private void generateWorkout() {
        List<String> targetMuscles = getSelectedMuscles();
        if (targetMuscles.isEmpty()) {
            showToast(getString(R.string.workout_builder_error_muscle_required));
            return;
        }
        List<String> selectedEquipment = getSelectedEquipment();

        int workSecs, restSecs;
        int intensityId = rgIntensity.getCheckedRadioButtonId();
        if (intensityId == R.id.rb_intensity_low) {
            workSecs = 30; restSecs = 30;
        } else if (intensityId == R.id.rb_intensity_hiit) {
            workSecs = 20; restSecs = 10;
        } else if (intensityId == R.id.rb_intensity_custom) {
            String workText = etCustomWork.getText().toString().trim();
            String restText = etCustomRest.getText().toString().trim();
            if (TextUtils.isEmpty(workText) || TextUtils.isEmpty(restText)) {
                showToast("Please enter custom intervals.");
                return;
            }
            try {
                workSecs = Integer.parseInt(workText);
                restSecs = Integer.parseInt(restText);
                if (workSecs <= 0) {
                    showToast("Work time must be at least 1 second.");
                    return;
                }
                if (restSecs < 0) {
                    showToast("Rest time cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showToast("Invalid interval values.");
                return;
            }
        } else {
            workSecs = 45; restSecs = 15;
        }

        WorkoutGenerationConstraints constraints;
        if (rgBuildMode.getCheckedRadioButtonId() == R.id.rb_mode_time) {
            String timeText = etTargetTime.getText().toString().trim();
            if (TextUtils.isEmpty(timeText)) {
                showToast(getString(R.string.workout_builder_error_time_required));
                return;
            }
            try {
                int totalMinutes = Integer.parseInt(timeText);
                if (totalMinutes <= 0) {
                    showToast("Time must be at least 1 minute.");
                    return;
                }
                constraints = WorkoutGenerationConstraints.createTimeBased(enumMapper, selectedEquipment, targetMuscles,
                        totalMinutes * 60, workSecs, restSecs);
            } catch (NumberFormatException e) {
                showToast("Invalid time value.");
                return;
            }
        } else {
            String countText = etExerciseCount.getText().toString().trim();
            if (TextUtils.isEmpty(countText)) {
                showToast(getString(R.string.workout_builder_error_exercise_count_required));
                return;
            }
            try {
                int exerciseCount = Integer.parseInt(countText);
                if (exerciseCount <= 0) {
                    showToast("Exercise count must be at least 1.");
                    return;
                }
                constraints = WorkoutGenerationConstraints.createCountBased(enumMapper, selectedEquipment, targetMuscles, exerciseCount, workSecs, restSecs);
            } catch (NumberFormatException e) {
                showToast("Invalid exercise count.");
                return;
            }
        }

        try {
            Workout workout = workoutBuilderUseCase.generateWorkout(constraints);
            String summary = buildSummaryText(constraints);
            String preview = buildPreviewText(workout);
            
            WorkoutGeneratorResultFragment resultFragment = WorkoutGeneratorResultFragment.newInstance(workout, summary, preview);
            resultFragment.setDependencies(workoutUseCase, exerciseUseCase, sessionHistoryUseCase, caloriesEstimationUseCase, this);
            resultFragment.show(getParentFragmentManager(), "generator_result");
        } catch (Exception ex) {
            showToast(ex.getMessage());
        }
    }

    private String buildSummaryText(WorkoutGenerationConstraints constraints) {
        String muscles = constraints.getTargetMuscleGroups().stream()
                .map(MuscleGroup::getLabel).collect(Collectors.joining(", "));
        String equipment = constraints.getSelectedEquipment().isEmpty()
                ? getString(R.string.workout_builder_equipment_any)
                : constraints.getSelectedEquipment().stream()
                .map(EquipmentType::getLabel).collect(Collectors.joining(", "));

        if (constraints.isTimeBased()) {
            return String.format("Time: %d min | Intensity: %ds/%ds\nMuscles: %s\nEquipment: %s",
                    constraints.getTargetDurationSeconds() / 60,
                    constraints.getWorkSeconds(),
                    constraints.getRestSeconds(),
                    muscles, equipment);
        } else {
            return String.format("Exercises: %d | Intensity: %ds/%ds\nMuscles: %s\nEquipment: %s",
                    constraints.getTargetExerciseCount(),
                    constraints.getWorkSeconds(),
                    constraints.getRestSeconds(),
                    muscles, equipment);
        }
    }

    private String buildPreviewText(Workout workout) {
        StringBuilder preview = new StringBuilder();
        preview.append(getString(R.string.workout_builder_preview_header)).append("\n");
        preview.append(String.format("Structure: %d Rounds of %d Exercises", 
                workout.getSets(), workout.getSteps().size())).append("\n\n");

        List<Exercise> exercises = workoutUseCase.getExercisesForWorkout(workout);
        List<WorkoutStep> steps = workout.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            WorkoutStep step = steps.get(i);
            String exerciseName = i < exercises.size() ? exercises.get(i).getName() : step.getExerciseId();
            preview.append(i + 1)
                    .append(". ")
                    .append(exerciseName)
                    .append(" (")
                    .append(step.getWorkSeconds())
                    .append("s work / ")
                    .append(step.getRestSeconds())
                    .append("s rest)")
                    .append("\n");
        }
        return preview.toString().trim();
    }

    @Override
    public void onRegenerate() {
        generateWorkout();
    }

    @Override
    public void onManualEdit(Workout workout) {
        WorkoutEditorFragment fragment = WorkoutEditorFragment.newInstance(workout.getId());
        fragment.setDependencies(workoutUseCase, exerciseUseCase);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStartNow(Workout workout) {
        LiveWorkoutFragment fragment = LiveWorkoutFragment.newInstance(workout.getId());
        fragment.setDependencies(workoutUseCase, exerciseUseCase, sessionHistoryUseCase, caloriesEstimationUseCase);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAdjustSettings() {
        // Just dismisses the dialog, user is already on the builder screen
    }

    private List<String> getSelectedMuscles() {
        List<String> muscles = new ArrayList<>();
        for (Map.Entry<MuscleGroup, CheckBox> entry : muscleCheckBoxes.entrySet()) {
            if (entry.getValue().isChecked()) {
                muscles.add(entry.getKey().getLabel());
            }
        }
        return muscles;
    }

    private List<String> getSelectedEquipment() {
        List<String> equipment = new ArrayList<>();
        for (Map.Entry<EquipmentType, CheckBox> entry : equipmentCheckBoxes.entrySet()) {
            if (entry.getValue().isChecked()) {
                equipment.add(entry.getKey().getLabel());
            }
        }
        return equipment;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
