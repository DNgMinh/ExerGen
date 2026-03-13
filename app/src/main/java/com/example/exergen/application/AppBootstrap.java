package com.example.exergen.application;

import android.app.Application;

import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.business.repository.IWorkoutRepository;
import com.example.exergen.application.persistence.ExerciseRepositorySQLite;
import com.example.exergen.persistence.ExerciseRepositoryStub;
import com.example.exergen.application.persistence.SessionHistoryRepositorySQLite;
import com.example.exergen.persistence.SessionHistoryRepositoryStub;
import com.example.exergen.application.persistence.WorkoutRepositorySQLite;
import com.example.exergen.persistence.WorkoutRepositoryStub;

public final class AppBootstrap {

    private static AppBootstrap instance;

    // SWITCH DATA SOURCE HERE
    // Set to 'false' for SQLite
    // Set to 'true' for Stub
    private static final boolean USE_STUB = false;

    public static void init(Application app) {
        if (instance != null)
            return;
        instance = new AppBootstrap(app);
    }

    public static AppBootstrap get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "AppBootstrap not initialized. Call AppBootstrap.init(...) in Application.onCreate().");
        }
        return instance;
    }

    public final WorkoutUseCase workoutUseCase;
    public final WorkoutBuilderUseCase workoutBuilderUseCase;
    public final SessionHistoryUseCase sessionHistoryUseCase;
    public final StatisticsUseCase statisticsUseCase;
    public final ExerciseService exerciseService;

    private AppBootstrap(Application app) {
        IWorkoutRepository workoutRepository;
        IExerciseRepository exerciseRepository;
        ISessionHistoryRepository sessionHistoryRepository;

        if (USE_STUB) {
            workoutRepository = new WorkoutRepositoryStub();
            exerciseRepository = new ExerciseRepositoryStub();
            sessionHistoryRepository = new SessionHistoryRepositoryStub();
        } else {
            workoutRepository = new WorkoutRepositorySQLite(app);
            exerciseRepository = new ExerciseRepositorySQLite(app, new EnumMapper());
            sessionHistoryRepository = new SessionHistoryRepositorySQLite(app);
        }

        new Thread(() -> {
            workoutRepository.seedData();
            exerciseRepository.seedData();
        }).start();

        this.exerciseService = new ExerciseService(exerciseRepository);
        this.workoutUseCase = new WorkoutUseCase(workoutRepository, exerciseService);
        this.workoutBuilderUseCase = new WorkoutBuilderUseCase(exerciseService);
        this.sessionHistoryUseCase = new SessionHistoryUseCase(sessionHistoryRepository);
        this.statisticsUseCase = new StatisticsUseCase(sessionHistoryRepository);
    }
}
