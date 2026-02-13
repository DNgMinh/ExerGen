package com.example.exergen.application;

import android.app.Application;

import com.example.exergen.persistence.repository.ExerciseRepositoryStub;
import com.example.exergen.persistence.repository.WorkoutRepositoryStub;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.persistence.repository.ExerciseRepository;
import com.example.exergen.persistence.repository.WorkoutRepository;

public final class AppBootstrap {

    private static AppBootstrap instance;

    public static void init(Application app) {
        if (instance != null) return;
        instance = new AppBootstrap(app);
    }

    public static AppBootstrap get() {
        if (instance == null) {
            throw new IllegalStateException("AppBootstrap not initialized. Call AppBootstrap.init(...) in Application.onCreate().");
        }
        return instance;
    }

    // Expose use cases to presentation
    public final WorkoutUseCase workoutUseCase;
    public final ExerciseService exerciseService;

    private AppBootstrap(Application app) {
        // Stub persistence for Iteration 1
        WorkoutRepository workoutRepository = new WorkoutRepositoryStub();
        ExerciseRepository exerciseRepository = new ExerciseRepositoryStub();

        // Business use case
        this.workoutUseCase = new WorkoutUseCase(workoutRepository);
        this.exerciseService = new ExerciseService(exerciseRepository);
    }
}
