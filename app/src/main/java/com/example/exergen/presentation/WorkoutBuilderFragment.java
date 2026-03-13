package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.business.service.WorkoutPreviewData;
import com.example.exergen.business.service.WorkoutPreviewItem;
import com.example.exergen.business.service.WorkoutPreviewMapper;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutBuilderFragment extends Fragment {
    private static final String KEY_EXERCISE_COUNT = "builder_exercise_count";
    private static final String KEY_MUSCLE_CHEST = "builder_muscle_chest";
    private static final String KEY_MUSCLE_LEGS = "builder_muscle_legs";
    private static final String KEY_MUSCLE_BACK = "builder_muscle_back";
    private static final String KEY_MUSCLE_SHOULDERS = "builder_muscle_shoulders";
    private static final String KEY_MUSCLE_BICEPS = "builder_muscle_biceps";
    private static final String KEY_MUSCLE_TRICEPS = "builder_muscle_triceps";
    private static final String KEY_EQUIP_BODYWEIGHT = "builder_equipment_bodyweight";
    private static final String KEY_EQUIP_DUMBBELLS = "builder_equipment_dumbbells";
    private static final String KEY_EQUIP_BARBELL = "builder_equipment_barbell";
    private static final String KEY_EQUIP_EZ_CURL_BAR = "builder_equipment_ez_curl_bar";
    private static final String KEY_EQUIP_MACHINE = "builder_equipment_machine";
    private static final String KEY_EQUIP_CABLE = "builder_equipment_cable";
    private static final String KEY_SUMMARY = "builder_summary";
    private static final String KEY_PREVIEW = "builder_preview";
    private static final String KEY_PREVIEW_MODE = "builder_preview_mode";

    private WorkoutBuilderUseCase workoutBuilderUseCase;
    private WorkoutUseCase workoutUseCase;
    private ExerciseService exerciseService;
    private WorkoutPreviewMapper workoutPreviewMapper;
    private Workout lastGeneratedWorkout;

    private EditText etExerciseCount;
    private CheckBox cbMuscleChest;
    private CheckBox cbMuscleLegs;
    private CheckBox cbMuscleBack;
    private CheckBox cbMuscleShoulders;
    private CheckBox cbMuscleBiceps;
    private CheckBox cbMuscleTriceps;
    private CheckBox cbEquipmentBodyweight;
    private CheckBox cbEquipmentDumbbells;
    private CheckBox cbEquipmentBarbell;
    private CheckBox cbEquipmentEzCurlBar;
    private CheckBox cbEquipmentMachine;
    private CheckBox cbEquipmentCable;
    private TextView tvBuilderSummary;
    private TextView tvBuilderPreview;
    private Button btnGenerateWorkout;
    private LinearLayout previewActionContainer;
    private Button btnStartWorkout;
    private Button btnRegenerateWorkout;
    private Button btnEditConstraints;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (workoutBuilderUseCase == null || workoutUseCase == null || exerciseService == null) {
            workoutBuilderUseCase = AppBootstrap.get().workoutBuilderUseCase;
            workoutUseCase = AppBootstrap.get().workoutUseCase;
            exerciseService = AppBootstrap.get().exerciseService;
        }
        if (workoutPreviewMapper == null) {
            workoutPreviewMapper = new WorkoutPreviewMapper();
        }
    }

    public void setDependenciesForTesting(WorkoutBuilderUseCase workoutBuilderUseCase,
            WorkoutUseCase workoutUseCase,
            ExerciseService exerciseService) {
        setDependenciesForTesting(workoutBuilderUseCase, workoutUseCase, exerciseService, new WorkoutPreviewMapper());
    }

    public void setDependenciesForTesting(WorkoutBuilderUseCase workoutBuilderUseCase,
            WorkoutUseCase workoutUseCase,
            ExerciseService exerciseService,
            WorkoutPreviewMapper workoutPreviewMapper) {
        this.workoutBuilderUseCase = workoutBuilderUseCase;
        this.workoutUseCase = workoutUseCase;
        this.exerciseService = exerciseService;
        this.workoutPreviewMapper = workoutPreviewMapper;
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
        restoreState(savedInstanceState);

        btnGenerateWorkout.setOnClickListener(v -> generateAndPreviewWorkout());
        btnRegenerateWorkout.setOnClickListener(v -> generateAndPreviewWorkout());
        btnEditConstraints.setOnClickListener(v -> setPreviewMode(false));
        btnStartWorkout.setOnClickListener(v -> openLiveWorkout());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EXERCISE_COUNT, etExerciseCount.getText().toString());
        outState.putBoolean(KEY_MUSCLE_CHEST, cbMuscleChest.isChecked());
        outState.putBoolean(KEY_MUSCLE_LEGS, cbMuscleLegs.isChecked());
        outState.putBoolean(KEY_MUSCLE_BACK, cbMuscleBack.isChecked());
        outState.putBoolean(KEY_MUSCLE_SHOULDERS, cbMuscleShoulders.isChecked());
        outState.putBoolean(KEY_MUSCLE_BICEPS, cbMuscleBiceps.isChecked());
        outState.putBoolean(KEY_MUSCLE_TRICEPS, cbMuscleTriceps.isChecked());
        outState.putBoolean(KEY_EQUIP_BODYWEIGHT, cbEquipmentBodyweight.isChecked());
        outState.putBoolean(KEY_EQUIP_DUMBBELLS, cbEquipmentDumbbells.isChecked());
        outState.putBoolean(KEY_EQUIP_BARBELL, cbEquipmentBarbell.isChecked());
        outState.putBoolean(KEY_EQUIP_EZ_CURL_BAR, cbEquipmentEzCurlBar.isChecked());
        outState.putBoolean(KEY_EQUIP_MACHINE, cbEquipmentMachine.isChecked());
        outState.putBoolean(KEY_EQUIP_CABLE, cbEquipmentCable.isChecked());
        outState.putString(KEY_SUMMARY, tvBuilderSummary.getText().toString());
        outState.putString(KEY_PREVIEW, tvBuilderPreview.getText().toString());
        outState.putBoolean(KEY_PREVIEW_MODE, previewActionContainer.getVisibility() == View.VISIBLE);
    }

    private void bindViews(View view) {
        etExerciseCount = view.findViewById(R.id.et_exercise_count);
        cbMuscleChest = view.findViewById(R.id.cb_muscle_chest);
        cbMuscleLegs = view.findViewById(R.id.cb_muscle_legs);
        cbMuscleBack = view.findViewById(R.id.cb_muscle_back);
        cbMuscleShoulders = view.findViewById(R.id.cb_muscle_shoulders);
        cbMuscleBiceps = view.findViewById(R.id.cb_muscle_biceps);
        cbMuscleTriceps = view.findViewById(R.id.cb_muscle_triceps);
        cbEquipmentBodyweight = view.findViewById(R.id.cb_equipment_bodyweight);
        cbEquipmentDumbbells = view.findViewById(R.id.cb_equipment_dumbbells);
        cbEquipmentBarbell = view.findViewById(R.id.cb_equipment_barbell);
        cbEquipmentEzCurlBar = view.findViewById(R.id.cb_equipment_ez_curl_bar);
        cbEquipmentMachine = view.findViewById(R.id.cb_equipment_machine);
        cbEquipmentCable = view.findViewById(R.id.cb_equipment_cable);
        tvBuilderSummary = view.findViewById(R.id.tv_builder_summary);
        tvBuilderPreview = view.findViewById(R.id.tv_builder_preview);
        btnGenerateWorkout = view.findViewById(R.id.btn_generate_workout);
        previewActionContainer = view.findViewById(R.id.preview_action_container);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
        btnRegenerateWorkout = view.findViewById(R.id.btn_regenerate_workout);
        btnEditConstraints = view.findViewById(R.id.btn_edit_constraints);
    }

    private void restoreState(@Nullable Bundle state) {
        if (state == null) {
            return;
        }
        etExerciseCount.setText(state.getString(KEY_EXERCISE_COUNT, ""));
        cbMuscleChest.setChecked(state.getBoolean(KEY_MUSCLE_CHEST, false));
        cbMuscleLegs.setChecked(state.getBoolean(KEY_MUSCLE_LEGS, false));
        cbMuscleBack.setChecked(state.getBoolean(KEY_MUSCLE_BACK, false));
        cbMuscleShoulders.setChecked(state.getBoolean(KEY_MUSCLE_SHOULDERS, false));
        cbMuscleBiceps.setChecked(state.getBoolean(KEY_MUSCLE_BICEPS, false));
        cbMuscleTriceps.setChecked(state.getBoolean(KEY_MUSCLE_TRICEPS, false));
        cbEquipmentBodyweight.setChecked(state.getBoolean(KEY_EQUIP_BODYWEIGHT, false));
        cbEquipmentDumbbells.setChecked(state.getBoolean(KEY_EQUIP_DUMBBELLS, false));
        cbEquipmentBarbell.setChecked(state.getBoolean(KEY_EQUIP_BARBELL, false));
        cbEquipmentEzCurlBar.setChecked(state.getBoolean(KEY_EQUIP_EZ_CURL_BAR, false));
        cbEquipmentMachine.setChecked(state.getBoolean(KEY_EQUIP_MACHINE, false));
        cbEquipmentCable.setChecked(state.getBoolean(KEY_EQUIP_CABLE, false));
        tvBuilderSummary.setText(state.getString(KEY_SUMMARY, ""));
        tvBuilderPreview.setText(state.getString(KEY_PREVIEW, ""));
        setPreviewMode(state.getBoolean(KEY_PREVIEW_MODE, false));
    }

    private void generateAndPreviewWorkout() {
        String exerciseCountText = etExerciseCount.getText().toString().trim();
        if (TextUtils.isEmpty(exerciseCountText)) {
            showToast(getString(R.string.workout_builder_error_exercise_count_required));
            return;
        }

        int exerciseCount;
        try {
            exerciseCount = Integer.parseInt(exerciseCountText);
        } catch (NumberFormatException ex) {
            showToast(getString(R.string.workout_builder_error_exercise_count_invalid));
            return;
        }

        List<String> targetMuscles = getSelectedMuscles();
        if (targetMuscles.isEmpty()) {
            showToast(getString(R.string.workout_builder_error_muscle_required));
            return;
        }

        List<String> selectedEquipment = getSelectedEquipment();
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(selectedEquipment, targetMuscles,
                exerciseCount);

        String summaryText = getString(
                R.string.workout_builder_summary_format,
                constraints.getTargetExerciseCount(),
                constraints.getTargetMuscleGroups().stream().map(MuscleGroup::getLabel).collect(Collectors.joining(", ")),
                constraints.getSelectedEquipment().isEmpty()
                        ? getString(R.string.workout_builder_equipment_any)
                        : constraints.getSelectedEquipment().stream().map(EquipmentType::getLabel).collect(Collectors.joining(", ")));

        try {
            Workout generatedWorkout = workoutBuilderUseCase.generateWorkout(constraints);
            lastGeneratedWorkout = generatedWorkout;
            tvBuilderSummary.setText(summaryText);
            tvBuilderPreview.setText(buildPreviewText(generatedWorkout));
            setPreviewMode(true);
        } catch (IllegalArgumentException ex) {
            lastGeneratedWorkout = null;
            tvBuilderPreview.setText("");
            setPreviewMode(false);
            showToast(ex.getMessage());
        }
    }

    private void setPreviewMode(boolean enabled) {
        previewActionContainer.setVisibility(enabled ? View.VISIBLE : View.GONE);
        btnGenerateWorkout.setVisibility(enabled ? View.GONE : View.VISIBLE);
        setInputsEnabled(!enabled);
    }

    private void setInputsEnabled(boolean enabled) {
        etExerciseCount.setEnabled(enabled);
        cbMuscleChest.setEnabled(enabled);
        cbMuscleLegs.setEnabled(enabled);
        cbMuscleBack.setEnabled(enabled);
        cbMuscleShoulders.setEnabled(enabled);
        cbMuscleBiceps.setEnabled(enabled);
        cbMuscleTriceps.setEnabled(enabled);
        cbEquipmentBodyweight.setEnabled(enabled);
        cbEquipmentDumbbells.setEnabled(enabled);
        cbEquipmentBarbell.setEnabled(enabled);
        cbEquipmentEzCurlBar.setEnabled(enabled);
        cbEquipmentMachine.setEnabled(enabled);
        cbEquipmentCable.setEnabled(enabled);
    }

    private void openLiveWorkout() {
        if (lastGeneratedWorkout == null) {
            showToast(getString(R.string.workout_builder_error_generate_first));
            return;
        }
        workoutUseCase.saveWorkout(lastGeneratedWorkout);
        showToast(getString(R.string.workout_builder_saved_message));

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, LiveWorkoutFragment.newInstance(lastGeneratedWorkout.getId()))
                .addToBackStack(null)
                .commit();
    }

    private String buildPreviewText(Workout workout) {
        StringBuilder preview = new StringBuilder();
        WorkoutPreviewData previewData = workoutPreviewMapper.map(workout, exerciseService);
        preview.append(getString(R.string.workout_builder_preview_header)).append('\n');
        preview.append(getString(R.string.workout_builder_preview_count_format, previewData.getExerciseCount()));
        preview.append('\n');

        for (WorkoutPreviewItem item : previewData.getItems()) {
            preview.append(item.getSequence())
                    .append(". ")
                    .append(item.getExerciseName())
                    .append(" (")
                    .append(item.getWorkSeconds())
                    .append("s work / ")
                    .append(item.getRestSeconds())
                    .append("s rest)")
                    .append('\n');
        }
        return preview.toString().trim();
    }

    private List<String> getSelectedMuscles() {
        List<String> muscles = new ArrayList<>();
        if (cbMuscleChest.isChecked()) {
            muscles.add(MuscleGroup.CHEST.getLabel());
        }
        if (cbMuscleLegs.isChecked()) {
            muscles.add(MuscleGroup.LEGS.getLabel());
        }
        if (cbMuscleBack.isChecked()) {
            muscles.add(MuscleGroup.BACK.getLabel());
        }
        if (cbMuscleShoulders.isChecked()) {
            muscles.add(MuscleGroup.SHOULDERS.getLabel());
        }
        if (cbMuscleBiceps.isChecked()) {
            muscles.add(MuscleGroup.BICEPS.getLabel());
        }
        if (cbMuscleTriceps.isChecked()) {
            muscles.add(MuscleGroup.TRICEPS.getLabel());
        }
        return muscles;
    }

    private List<String> getSelectedEquipment() {
        List<String> equipment = new ArrayList<>();
        if (cbEquipmentBodyweight.isChecked()) {
            equipment.add(EquipmentType.BODYWEIGHT.getLabel());
        }
        if (cbEquipmentDumbbells.isChecked()) {
            equipment.add(EquipmentType.DUMBBELLS.getLabel());
        }
        if (cbEquipmentBarbell.isChecked()) {
            equipment.add(EquipmentType.BARBELL.getLabel());
        }
        if (cbEquipmentEzCurlBar.isChecked()) {
            equipment.add(EquipmentType.EZ_CURL_BAR.getLabel());
        }
        if (cbEquipmentMachine.isChecked()) {
            equipment.add(EquipmentType.MACHINE.getLabel());
        }
        if (cbEquipmentCable.isChecked()) {
            equipment.add(EquipmentType.CABLE.getLabel());
        }
        return equipment;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
