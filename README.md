# ExerGen

## Project Overview

ExerGen is an offline Android application that generates structured interval workouts based on a user’s available equipment, target muscle groups, and time constraints, and then guides and records the workout from start to finish.

---

## Demo Website

[**Try the ExerGen Web Demo**](https://dngminh.github.io/ExerGen/)

You can also download the latest APK directly from the [Demo Website](https://dngminh.github.io/ExerGen/) or [here (ExerGen_v3.0.apk)](docs/ExerGen_v3.0.apk).

## Core Features
- **Workout Builder**: Generates tailored routines using intelligent filtering across equipment and muscle groups.
- **Live Workout Mode**: An interactive session experience featuring a synchronized timer, exercise animations, and automatic phase transitions (Work/Rest).
- **Session History**: Persistent storage of completed workouts for long-term tracking.
- **Statistics Dashboard**: Aggregates raw data into visual trends, including calories burned and weekly activity.

---

## Vision Statement
[**Vision Statement**](docs/vision-statement.md) - Our goals and target audience.


---

## Project Retrospective
[**Project Retrospective**](docs/RETROSPECTIVE.md) - Lessons learned and architectural shifts during development.

---

## Credits & Sources
- We have used an open source resource to get the data and images for the exercises. Link provided below
- [free-exercise-db](https://github.com/yuhonas/free-exercise-db)

---

## Architecture & Package Structure

```
app/src/main/java/com/example/exergen/
│
├── application/         // Composition root and application-wide helpers
│   ├── helper/          // Database helpers (DatabaseHelper)
│   ├── ExerGenApp.java  // Application class
│   └── AppBootstrap.java // Dependency injection container
│
├── business/            // Domain logic and services
│   ├── exception/       // Domain-specific exceptions
│   ├── repository/      // Repository interfaces (IWorkoutRepository, etc.)
│   ├── service/         // Logic services (ExerciseService, IntervalTimer, StatisticsAggregationService)
│   ├── usecase/         // High-level business rules (WorkoutUseCase, SessionHistoryUseCase)
│   └── validation/      // Logic for validating business rules
│
├── model/               // Plain data objects (Exercise, Workout, SessionRecord)
│
├── persistence/         // Concrete repository implementations
│   ├── SQLite/          // SQL-based persistence (WorkoutRepositorySQLite, SessionHistoryRepositorySQLite, etc.)
│   └── Stub/            // In-memory data for testing (WorkoutRepositoryStub, SessionHistoryRepositoryStub, etc.)
│  
└── presentation/        // Android UI (Activities, Fragments, ViewModels, Adapters)
```

* [Architecture](ARCHITECTURE.md) Detailed breakdown of layers and package structure.

---

### Dependency Rules

* `presentation → business → persistence`
* `application` wires concrete implementations together using `AppBootstrap`.
* **No Android imports** (`android.*`, `androidx.*`) are allowed in `business`, `persistence` (except for context in SQLite), or `model` layers.
* Android-specific code belongs **only** in `presentation` and `application`.

---

## Persistence & Database

* **Iteration 1**: Used **Stub/Fake repository** for in-memory data.
* **Iteration 2**: Implemented **SQLite** via `SupportSQLiteOpenHelper` for persistent storage of workouts, exercises, and session history.

---

## Testing Strategy

### Unit Tests
**Location:** `app/src/test/java`

* Verifies business logic in isolation (e.g., `ExerciseService`, `WorkoutBuilderUseCase`).
* Uses JUnit 4 and Mockito.
* Relies on Stub repositories or Mocks to ensure tests run without Android dependencies.

### Integration Tests
**Location:** `app/src/androidTest/java`

* Verifies that different layers (Presentation, Business, Persistence) work together correctly.
* Uses AndroidJUnitRunner and Espresso for UI-related integration tests.

### UI/Espresso Tests
**Location:** `app/src/androidTest/java`

* Verifies that the user interface of an application behaves correctly from a user's perspective
* Uses AndroidJUnitRunner and Espresso.

**To run tests:**
1. Open the project in **Android Studio**.
2. In the Project view (left panel), navigate to `app/src/test/java` or `app/src/androidTest/java`.
3. Right-click the `com.example.exergen` package.
4. Select **Run 'Tests in 'com.example.exergen''**.

---

## Build & Run Instructions

### Using Android Studio
1. **Open the project**: Select the root folder in Android Studio.
2. **Gradle Sync**: Wait for the IDE to finish the synchronization.
3. **Run**: Click the green **Run** button or press `Shift + F10`.

---

## SDK & Tooling Requirements

### Android SDK
* `compileSdk = 34`
* `targetSdk = 34`
* '`minSdk = 26`'
### Java
* **Java 17**
* Kotlin is **not used** in the logic/persistence layers of this project.

### Tools
* Android Studio
* Android Emulator (Pixel 9 device, Android 15 API level 35)
* Git / GitLab (hosted at code.cs.umanitoba.ca)

---

## Branching

Our team employs a feature branch workflow to ensure code stability and organized collaboration throughout development. The `main` branch is strictly reserved for production-ready releases, while `dev` serves as the primary integration branch for ongoing work. All new features are developed in isolation on dedicated branches created off of `dev`, using the naming convention `feature/feature-name`. These feature branches are only merged back into develop via merge requests after passing code review and successful unit tests.
