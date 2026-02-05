package com.example.exergen.application;

import android.app.Application;

import com.example.exergen.application.repository.WorkoutRepositoryStub;
import com.example.exergen.business.usecase.WorkoutUseCase;
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

    private AppBootstrap(Application app) {
        // Stub persistence for Iteration 1
        WorkoutRepository workoutRepository = new WorkoutRepositoryStub();

        // Business use case
        this.workoutUseCase = new WorkoutUseCase(workoutRepository);
    }
}
