package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.exergen.R;
import com.example.exergen.business.usecase.CaloriesEstimationUseCase;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.TimerMode;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Workout;

public class LiveWorkoutFragment extends Fragment {
    private static final String ARG_WORKOUT_ID = "workout_id";
    private static final String TAG = "LiveWorkoutFragment";

    private String workoutId;
    private Workout workout;
    private WorkoutUseCase workoutUseCase;
    private ExerciseUseCase exerciseUseCase;
    private SessionHistoryUseCase sessionHistoryUseCase;
    private CaloriesEstimationUseCase caloriesEstimationUseCase;
    private LiveWorkoutViewModel viewModel;
    private SoundFeedbackHelper soundFeedbackHelper;
    private ExerciseAnimationManager animationManager;

    private TextView tvTimer, tvPhase, tvWorkoutName, tvCurrentExercise, tvNextExercise, tvRoundIndicator;
    private TextView tvFinishCalories;
    private View finishStatsContainer;
    private ImageView ivAnimation;
    private Button btnStart, btnPause, btnCancel;
    private ImageButton btnExit;
    private View activeContainer;

    public static LiveWorkoutFragment newInstance(String workoutId) {
        LiveWorkoutFragment fragment = new LiveWorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORKOUT_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundFeedbackHelper = new SoundFeedbackHelper();
        if (workoutUseCase == null
                || exerciseUseCase == null
                || sessionHistoryUseCase == null
                || caloriesEstimationUseCase == null) {
            throw new IllegalStateException("LiveWorkoutFragment dependencies not provided");
        }
        if (getArguments() != null) {
            workoutId = getArguments().getString(ARG_WORKOUT_ID);
            workout = workoutUseCase.getWorkoutById(workoutId);
        }
        viewModel = new ViewModelProvider(this).get(LiveWorkoutViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupButtons();
        setupObservers();

        if (workout != null) {
            tvWorkoutName.setText(workout.getName());
            if (viewModel.getUiState().getValue() != null && viewModel.getUiState().getValue().isSetupVisible()) {
                viewModel.init(workout, 
                        workout.getWorkSeconds().get(0), 
                        workout.getRestSeconds().get(0), 
                        workout.getSets(), 
                        exerciseUseCase, 
                        sessionHistoryUseCase, 
                        caloriesEstimationUseCase);
            }
            updateRoundIndicator();
        }
        setBottomNavVisibility(View.GONE);
    }

    private void initializeViews(View view) {
        tvTimer = view.findViewById(R.id.tv_live_timer);
        tvPhase = view.findViewById(R.id.tv_live_phase);
        tvWorkoutName = view.findViewById(R.id.tv_workout_name);
        tvRoundIndicator = view.findViewById(R.id.tv_round_indicator);
        tvCurrentExercise = view.findViewById(R.id.tv_current_exercise);
        tvNextExercise = view.findViewById(R.id.tv_next_exercise);
        ivAnimation = view.findViewById(R.id.iv_exercise_animation);
        tvFinishCalories = view.findViewById(R.id.tv_finish_calories);
        finishStatsContainer = view.findViewById(R.id.finish_stats_container);
        
        btnStart = view.findViewById(R.id.btn_live_start);
        btnPause = view.findViewById(R.id.btn_live_pause);
        btnCancel = view.findViewById(R.id.btn_live_cancel);
        btnExit = view.findViewById(R.id.btn_exit_workout);
        activeContainer = view.findViewById(R.id.active_exercise_container);
        animationManager = new ExerciseAnimationManager(ivAnimation, TAG);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> viewModel.start());
        btnPause.setOnClickListener(v -> viewModel.pause());
        btnCancel.setOnClickListener(v -> exitWorkout());
        btnExit.setOnClickListener(v -> confirmExit());
    }

    private void setupObservers() {
        viewModel.getTimeLeft().observe(getViewLifecycleOwner(), seconds -> {
            String timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d",
                    seconds / 60, seconds % 60);
            tvTimer.setText(timeString);
            if (Boolean.TRUE.equals(viewModel.getIsRunning().getValue()) && seconds > 0 && seconds <= 3) {
                soundFeedbackHelper.playCountdownBeep();
            }
        });

        viewModel.getPhase().observe(getViewLifecycleOwner(), phase -> {
            boolean isRunning = Boolean.TRUE.equals(viewModel.getIsRunning().getValue());
            if (phase == TimerMode.WORK) {
                if (isRunning) {
                    tvPhase.setText(getString(R.string.timer_work));
                    tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                    soundFeedbackHelper.playTransitionBeep();
                } else {
                    tvPhase.setText(getString(R.string.timer_ready));
                    tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
                }
            }
            else {
                tvPhase.setText(getString(R.string.timer_rest));
                tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
                tvCurrentExercise.setText("");
                animationManager.pause();
                ivAnimation.setImageDrawable(null);
                soundFeedbackHelper.playTransitionBeep();
            }
            updateRoundIndicator();
        });

        viewModel.getCurrentExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise != null) {
                if (viewModel.getPhase().getValue() == TimerMode.WORK) {
                    tvCurrentExercise.setText(exercise.getName());
                    animationManager.load(requireContext(), exercise.getImagePaths());
                    if (Boolean.TRUE.equals(viewModel.getIsRunning().getValue())) {
                        animationManager.resume();
                    }
                }
            }
        });

        viewModel.getNextExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise != null) {
                tvNextExercise.setText(getString(R.string.live_workout_next_label, exercise.getName()));
                tvNextExercise.setVisibility(View.VISIBLE);
            }
            else {
                tvNextExercise.setVisibility(View.GONE);
            }
        });

        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            activeContainer.setVisibility(state.isActiveVisible() ? View.VISIBLE : View.GONE);
            btnStart.setVisibility(state.isStartVisible() ? View.VISIBLE : View.GONE);
            btnPause.setVisibility(state.isPauseVisible() ? View.VISIBLE : View.GONE);
            btnCancel.setVisibility(state.isCancelVisible() ? View.VISIBLE : View.GONE);
            btnPause.setEnabled(state.isPauseEnabled());
            btnStart.setText(getString(state.getStartTextResId()));
            btnCancel.setText(state.getCancelTextResId());
            tvTimer.setVisibility(state.isTimerVisible() ? View.VISIBLE : View.GONE);

            if (state.isRunning()) {
                if (viewModel.getPhase().getValue() == TimerMode.WORK) {
                    animationManager.resume();
                    if (getString(R.string.timer_ready).equals(tvPhase.getText().toString())) {
                        tvPhase.setText(getString(R.string.timer_work));
                        tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                        soundFeedbackHelper.playTransitionBeep();
                    }
                }
            } else {
                animationManager.pause();
                if (!state.isFinished() && state.isActiveVisible() && !state.isSetupVisible()) {
                    tvPhase.setText(getString(R.string.timer_paused));
                }
            }

            if (state.isFinished()) {
                tvPhase.setText(getString(R.string.live_workout_finished));
                tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                tvCurrentExercise.setText("");
                tvNextExercise.setText("");
                tvRoundIndicator.setText("");
                
                // Show appealing finish stats
                ivAnimation.setVisibility(View.GONE);
                finishStatsContainer.setVisibility(View.VISIBLE);
                
                int cals = viewModel.calculateTotalWorkoutCalories();
                tvFinishCalories.setText(cals + " kcal");
                
                animationManager.stop();
            } else {
                ivAnimation.setVisibility(View.VISIBLE);
                finishStatsContainer.setVisibility(View.GONE);
            }
            
            // Ensure bottom nav is always hidden in this fragment, regardless of finished state
            setBottomNavVisibility(View.GONE);
        });
    }

    private void updateRoundIndicator() {
        if (workout != null && viewModel != null) {
            int currentRound = viewModel.getCurrentRound();
            int totalRounds = workout.getSets();
            tvRoundIndicator.setText(String.format("Round %d / %d", currentRound, totalRounds));
        }
    }

    private void confirmExit() {
        if (Boolean.TRUE.equals(viewModel.getIsFinished().getValue())) {
            exitWorkout();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.live_workout_exit_confirm)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> exitWorkout())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void exitWorkout() {
        if (!Boolean.TRUE.equals(viewModel.getIsFinished().getValue())) {
            viewModel.stop();
        }
        setBottomNavVisibility(View.VISIBLE);
        getParentFragmentManager().popBackStack();
    }

    private void setBottomNavVisibility(int visibility) {
        if (getActivity() != null) {
            View nav = getActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) nav.setVisibility(visibility);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundFeedbackHelper != null) {
            soundFeedbackHelper.release();
        }
        if (animationManager != null) {
            animationManager.stop();
        }
        setBottomNavVisibility(View.VISIBLE);
    }
}
