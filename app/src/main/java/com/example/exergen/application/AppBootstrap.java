package com.example.exergen.application;

import android.app.Application;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.business.usecase.StatisticsUseCase;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.business.repository.IWorkoutRepository;
import com.example.exergen.persistence.ExerciseRepositorySQLite;
import com.example.exergen.persistence.SessionHistoryRepositorySQLite;
import com.example.exergen.persistence.WorkoutRepositorySQLite;

public final class AppBootstrap {

    private static AppBootstrap instance;

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

    // Expose use cases to presentation
    public final WorkoutUseCase workoutUseCase;
    public final WorkoutBuilderUseCase workoutBuilderUseCase;
    public final SessionHistoryUseCase sessionHistoryUseCase;
    public final StatisticsUseCase statisticsUseCase;
    public final ExerciseService exerciseService;

    private AppBootstrap(Application app) {
        IWorkoutRepository workoutRepository = new WorkoutRepositorySQLite(app);
        IExerciseRepository exerciseRepository = new ExerciseRepositorySQLite(app);
        ISessionHistoryRepository sessionHistoryRepository = new SessionHistoryRepositorySQLite(app);

        // Seed data for workout and exercise repositories
        workoutRepository.seedData();
        exerciseRepository.seedData();

        this.exerciseService = new ExerciseService(exerciseRepository);
        this.workoutUseCase = new WorkoutUseCase(workoutRepository, exerciseService);
        this.workoutBuilderUseCase = new WorkoutBuilderUseCase(exerciseService);
        this.sessionHistoryUseCase = new SessionHistoryUseCase(sessionHistoryRepository);
        this.statisticsUseCase = new StatisticsUseCase(sessionHistoryRepository);
    }
}