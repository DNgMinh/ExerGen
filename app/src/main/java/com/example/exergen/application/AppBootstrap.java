package com.example.exergen.application;

import android.app.Application;

import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.persistence.repository.IExerciseRepository;
import com.example.exergen.persistence.repository.ISessionHistoryRepository;
import com.example.exergen.persistence.repository.IWorkoutRepository;
import com.example.exergen.persistence.ExerciseRepositorySQLite;
import com.example.exergen.persistence.ExerciseRepositoryStub;
import com.example.exergen.persistence.SessionHistoryRepositorySQLite;
import com.example.exergen.persistence.SessionHistoryRepositoryStub;
import com.example.exergen.persistence.WorkoutRepositorySQLite;
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
    public final ExerciseUseCase exerciseUseCase;
    public final ExerciseService exerciseService;
    public final IEnumMapper enumMapper;

    private AppBootstrap(Application app) {
        IWorkoutRepository workoutRepository;
        IExerciseRepository exerciseRepository;
        ISessionHistoryRepository sessionHistoryRepository;

        this.enumMapper = new EnumMapper();

        if (USE_STUB) {
            workoutRepository = new WorkoutRepositoryStub();
            exerciseRepository = new ExerciseRepositoryStub();
            sessionHistoryRepository = new SessionHistoryRepositoryStub();
        } else {
            workoutRepository = new WorkoutRepositorySQLite(app);
            exerciseRepository = new ExerciseRepositorySQLite(app, enumMapper);
            sessionHistoryRepository = new SessionHistoryRepositorySQLite(app);
        }

        // Seed synchronously so initial screens never race data availability.
        workoutRepository.seedData();
        exerciseRepository.seedData();

        this.exerciseService = new ExerciseService(exerciseRepository);
        this.exerciseUseCase = new ExerciseUseCase(exerciseService);
        this.workoutUseCase = new WorkoutUseCase(workoutRepository, exerciseService);
        this.workoutBuilderUseCase = new WorkoutBuilderUseCase(exerciseService, enumMapper);
        this.sessionHistoryUseCase = new SessionHistoryUseCase(sessionHistoryRepository);
        this.statisticsUseCase = new StatisticsUseCase(sessionHistoryRepository);
    }
}
