# ARCHITECTURE

![architecture](Architecture-Diagram.png)

### Major Packages

- **`presentation`** - User interface components and views
- **`business`** - Business logic and use cases
- **`persistence`** - Data access layer and repositories
- **`application`** - Configuration and setup
- **`model`** - Domain specific objects

## Layered Architecture

### Presentation Layer (UI)

- **[`MainActivity`](app/src/main/java/com/example/exergen/presentation/MainActivity.java)**
  - Entry point of the app
  - Hosts the bottom navigation and manages fragment switching
- **[`ExercisesFragment`](app/src/main/java/com/example/exergen/presentation/ExercisesFragment.java)**
  - Displays the searchable and filterable exercise library
- **[`ExerciseDetailFragment`](app/src/main/java/com/example/exergen/presentation/ExerciseDetailFragment.java)**
  - Shows comprehensive info for an exercise, including description and images
- **[`WorkoutsFragment`](app/src/main/java/com/example/exergen/presentation/WorkoutsFragment.java)**
  - Lists saved workout routines for selection
- **[`WorkoutDetailFragment`](app/src/main/java/com/example/exergen/presentation/WorkoutDetailFragment.java)**
  - Detailed view of a saved workout routine and its exercises
- **[`WorkoutEditorFragment`](app/src/main/java/com/example/exergen/presentation/WorkoutEditorFragment.java)**
  - Interactive editor to create or modify workout routines and steps
- **[`WorkoutBuilderFragment`](app/src/main/java/com/example/exergen/presentation/WorkoutBuilderFragment.java)**
  - Form interface to input constraints for workout generation
- **[`WorkoutGeneratorResultFragment`](app/src/main/java/com/example/exergen/presentation/WorkoutGeneratorResultFragment.java)**
  - Displays and allows saving of a generated workout routine
- **[`LiveWorkoutFragment`](app/src/main/java/com/example/exergen/presentation/LiveWorkoutFragment.java)**
  - Interactive screen that guides the user through an active workout session
- **[`StatsFragment`](app/src/main/java/com/example/exergen/presentation/StatsFragment.java)**
  - Visualization of user performance history and trends
- **[`SessionDetailFragment`](app/src/main/java/com/example/exergen/presentation/SessionDetailFragment.java)**
  - Bottom sheet showing metrics for a specific completed session
- **[`TimerFragment`](app/src/main/java/com/example/exergen/presentation/TimerFragment.java)**
  - Quick standalone timer for interval training
- **[`LiveWorkoutViewModel`](app/src/main/java/com/example/exergen/presentation/LiveWorkoutViewModel.java)** / **[`TimerViewModel`](app/src/main/java/com/example/exergen/presentation/TimerViewModel.java)**
  - Coordinate session state and timing logic, surviving configuration changes
- **[`ExerciseAnimationManager`](app/src/main/java/com/example/exergen/presentation/ExerciseAnimationManager.java)**
  - Handles frame-by-frame animation loading for exercise demonstrations
- **[`SoundFeedbackHelper`](app/src/main/java/com/example/exergen/presentation/SoundFeedbackHelper.java)**
  - Centralizes audio cues for timer ticks and phase transitions
- **Adapters** (`ExerciseAdapter`, `WorkoutAdapter`, `SessionHistoryAdapter`, `WorkoutStepAdapter`)
  - Bind domain and presentation models to RecyclerView components

### Business Layer

#### `business/usecase/`

- **[`WorkoutUseCase`](app/src/main/java/com/example/exergen/business/usecase/WorkoutUseCase.java)**
  - High-level operations for workout routine management
- **[`WorkoutBuilderUseCase`](app/src/main/java/com/example/exergen/business/usecase/WorkoutBuilderUseCase.java)**
  - Orchestrates the automated generation of workouts based on constraints
- **[`SessionHistoryUseCase`](app/src/main/java/com/example/exergen/business/usecase/SessionHistoryUseCase.java)**
  - Logic for logging and retrieving completed workout session records
- **[`StatisticsUseCase`](app/src/main/java/com/example/exergen/business/usecase/StatisticsUseCase.java)**
  - Prepares aggregated data for the statistics dashboard
- **[`CaloriesEstimationUseCase`](app/src/main/java/com/example/exergen/business/usecase/CaloriesEstimationUseCase.java)**
  - Domain-specific logic for calculating calorie burn during sessions
- **[`TimerSessionUseCase`](app/src/main/java/com/example/exergen/business/usecase/TimerSessionUseCase.java)**
  - Encapsulates the lifecycle and state transitions of an active timer session

#### `business/service/`

- **[`ExerciseService`](app/src/main/java/com/example/exergen/business/service/ExerciseService.java)**
  - Core business rules for searching and filtering the exercise library
- **[`ExerciseConstraintMatcher`](app/src/main/java/com/example/exergen/business/service/ExerciseConstraintMatcher.java)**
  - Scoring and matching logic used during workout generation
- **[`IntervalTimer`](app/src/main/java/com/example/exergen/business/service/IntervalTimer.java)**
  - Engine for work/rest countdowns and phase switching
- **[`SessionManager`](app/src/main/java/com/example/exergen/business/service/SessionManager.java)**
  - Tracks the current step and progress through a workout routine
- **[`WorkoutMetricsService`](app/src/main/java/com/example/exergen/business/service/WorkoutMetricsService.java)**
  - Calculates routine-level metadata like total duration
- **[`CaloriesEstimationService`](app/src/main/java/com/example/exergen/business/service/CaloriesEstimationService.java)**
  - Mathematical implementation for intensity-based energy expenditure
- **[`StatisticsAggregationService`](app/src/main/java/com/example/exergen/business/service/StatisticsAggregationService.java)**
  - Processes lists of session records into summaries and trend points

### Persistence Layer

- **Repository Interfaces** (`IExerciseRepository`, `IWorkoutRepository`, `ISessionHistoryRepository`)
  - Define the data access contracts, decoupling business logic from implementation
- **SQLite Implementations** (`ExerciseRepositorySQLite`, `WorkoutRepositorySQLite`, `SessionHistoryRepositorySQLite`)
  - Provide durable storage using Android's native SQLite database
- **Stub Implementations** (`ExerciseRepositoryStub`, `WorkoutRepositoryStub`, `SessionHistoryRepositoryStub`)
  - Lightweight, in-memory repositories used for fast unit testing and Iteration 1 development
- **[`CSVParser`](app/src/main/java/com/example/exergen/persistence/CSVParser.java)**
  - Utility to seed the database from raw exercise data files

### Application

- **[`AppBootstrap`](app/src/main/java/com/example/exergen/application/AppBootstrap.java)**
  - Central dependency injection container that wires concrete persistence into business services

### Model

- **[`Exercise`](app/src/main/java/com/example/exergen/model/Exercise.java)**
  - Domain model for an exercise (id, name, muscles, equipment, etc.)
- **[`Workout`](app/src/main/java/com/example/exergen/model/Workout.java)**
  - Domain model for a routine, storing immutable steps and repetition count
- **[`WorkoutStep`](app/src/main/java/com/example/exergen/model/WorkoutStep.java)**
  - Encapsulates a specific exercise ID and its corresponding work/rest timing
- **[`SessionRecord`](app/src/main/java/com/example/exergen/model/SessionRecord.java)**
  - Stores data about a completed workout (date, performance metrics, calories)
- **[`MuscleGroup`](app/src/main/java/com/example/exergen/model/MuscleGroup.java)** / **[`EquipmentType`](app/src/main/java/com/example/exergen/model/EquipmentType.java)**
  - Strongly-typed Enums representing domain categories and metadata
- **[`StatisticsSummary`](app/src/main/java/com/example/exergen/model/StatisticsSummary.java)** / **[`WeeklyTrendPoint`](app/src/main/java/com/example/exergen/model/WeeklyTrendPoint.java)**
  - Value objects designed to transport aggregated statistical data to the UI
