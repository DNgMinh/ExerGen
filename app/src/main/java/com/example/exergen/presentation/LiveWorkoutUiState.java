package com.example.exergen.presentation;

import com.example.exergen.R;

public class LiveWorkoutUiState {
    private final boolean setupVisible;
    private final boolean activeVisible;
    private final boolean startVisible;
    private final boolean pauseVisible;
    private final boolean cancelVisible;
    private final boolean timerVisible;
    private final boolean pauseEnabled;
    private final boolean running;
    private final boolean finished;
    private final boolean showBottomNav;
    private final int startTextResId;
    private final int cancelTextResId;

    private LiveWorkoutUiState(
            boolean setupVisible,
            boolean activeVisible,
            boolean startVisible,
            boolean pauseVisible,
            boolean cancelVisible,
            boolean timerVisible,
            boolean pauseEnabled,
            boolean running,
            boolean finished,
            boolean showBottomNav,
            int startTextResId,
            int cancelTextResId) {
        this.setupVisible = setupVisible;
        this.activeVisible = activeVisible;
        this.startVisible = startVisible;
        this.pauseVisible = pauseVisible;
        this.cancelVisible = cancelVisible;
        this.timerVisible = timerVisible;
        this.pauseEnabled = pauseEnabled;
        this.running = running;
        this.finished = finished;
        this.showBottomNav = showBottomNav;
        this.startTextResId = startTextResId;
        this.cancelTextResId = cancelTextResId;
    }

    public static LiveWorkoutUiState setup() {
        return new LiveWorkoutUiState(
                true, false, true, false, true, true,
                false, false, false, false,
                R.string.btn_start, android.R.string.cancel);
    }

    public static LiveWorkoutUiState activeRunning() {
        return new LiveWorkoutUiState(
                false, true, false, true, false, true,
                true, true, false, false,
                R.string.btn_resume, android.R.string.cancel);
    }

    public static LiveWorkoutUiState activePaused() {
        return new LiveWorkoutUiState(
                false, true, true, true, false, true,
                false, false, false, false,
                R.string.btn_resume, android.R.string.cancel);
    }

    public static LiveWorkoutUiState finished() {
        return new LiveWorkoutUiState(
                false, true, false, false, true, false,
                false, false, true, true,
                R.string.btn_resume, R.string.btn_back);
    }

    public boolean isSetupVisible() { return setupVisible; }
    public boolean isActiveVisible() { return activeVisible; }
    public boolean isStartVisible() { return startVisible; }
    public boolean isPauseVisible() { return pauseVisible; }
    public boolean isCancelVisible() { return cancelVisible; }
    public boolean isTimerVisible() { return timerVisible; }
    public boolean isPauseEnabled() { return pauseEnabled; }
    public boolean isRunning() { return running; }
    public boolean isFinished() { return finished; }
    public boolean isShowBottomNav() { return showBottomNav; }
    public int getStartTextResId() { return startTextResId; }
    public int getCancelTextResId() { return cancelTextResId; }
}
