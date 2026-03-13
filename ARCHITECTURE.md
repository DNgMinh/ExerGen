
# ARCHITECTURE
### Major Packages
- **`presentation`** - User interface components and views
- **`business`** - Business logic and use cases
- **`persistance`** - Data access layer and repositories
- **`application`** - Configuration and setup
- **`model`** - Domain specific objects

## Layered Architecture

### Presentation Layer (UI)
- **[`MainActivity`](app/src/main/java/com/example/exergen/presentation/MainActivity.java)**
    - Entry point of the app**
    - Entry point of the app
    - Displays results to the user (full instructions, equipment, muscles, duration)
    - User input handling
    - Displays application state
- **[`AddFragment`](app/src/main/java/com/example/exergen/presentation/AddFragment.java)**
    - Displays a list of all exercises
- **[`StatsFragment`](app/src/main/java/com/example/exergen/presentation/StatsFragment.java)**
    - Displays statistics about user's workout (in progress)
- **[`TimerFragment`](app/src/main/java/com/example/exergen/presentation/TimerFragment.java)**
    - Displays the workout timer (includes working, resting time and num of sets)
- **[`ExerciseAdapter`](app/src/main/java/com/example/exergen/presentation/ExerciseAdapter.java)**
    - Adapts a list of exercises to be displayed
- **[`WorkoutAdapter`](app/src/main/java/com/example/exergen/presentation/WorkoutAdapter.java)**
    - Adapts a list of workouts to be displayed
- **[`WorkoutsFragment`](app/src/main/java/com/example/exergen/presentation/WorkoutsFragment.java)**
    - Displays a list of user's saved workout
- **[`ExerciseDetailFragment`](app/src/main/java/com/example/exergen/presentation/ExerciseDetailFragment.java)**
    - Displays detailed information about an exercise

### Business Layer

#### `business/usecase/`

- **[`WorkoutUseCase`](app/src/main/java/com/example/exergen/business/usecase/WorkoutUseCase.java)**
    - Main facade for the application’s domain operations in Iteration 1

#### `business/service/`
- **[`IntervalTimer`](app/src/main/java/com/example/exergen/business/service/IntervalTimer.java)**
    - Core timer implementation for workout intervals
-**[`TimerObserver`](app/src/main/java/com/example/exergen/business/service/TimerObserver.java)**
    - Interface for components that react to timer events
- **[`ExerciseService`](app/src/main/java/com/example/exergen/business/service/ExerciseService.java)**
    - Provides operations related to the exercise library from a repository

### Persistence Layer
- **[`ExerciseRepository`](app/src/main/java/com/example/exergen/persistence/repository/ExerciseRepository.java)**
    - Interface for retrieving exercises.
- **[`WorkoutRepository`](app/src/main/java/com/example/exergen/persistence/repository/WorkoutRepository.java)**
    - Interface for saving and retrieving workouts.

### Model 
- **[`Exercise`](app/src/main/java/com/example/exergen/model/Exercise.java)**
    - Represents a single exercise in the library (id, name, muscle group, equipment, instructions, intensity)
- **[`Workout`](app/src/main/java/com/example/exergen/model/Workout.java)**
    - Represents a workout built from one or more `Exercise` instances





