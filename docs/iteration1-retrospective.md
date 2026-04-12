# Iteration 1 Retrospective - Group 15 GPA=GPT

**Date: February 16, 2026**

  

## Overview

Iteration 1 focused on establishing the core infrastructure and implementing the Exercise Library feature and interval Timer functionality. The team emphasized test-driven development and clean architecture patterns.

  

  

## Learning Points

  

### What We Learned:

1.  **TDD is really helpful**: Starting with tests helped catch interface design issues early and ensured code correctness.

2.  **Stub Data is Essential for Iteration 1**: Having ExerciseRepositoryStub with seed data allowed UI development to proceed in parallel without database implementation.

3.  **Fragment Navigation Patterns**: Working with Android fragments revealed the importance of proper fragment lifecycle management.

4.  **Timer Logic Complexity**: The IntervalTimer business logic required careful state management (work/rest phases, set counting).

  

### Areas for Improvement:

-  **Database Implementation Deferred**: Stub repositories will need to be replaced with real persistence (Room/SQLite) in Iteration 2.

-  **UI Polish**: Timer UI works but needs better visual feedback and orientation handling.

-  **Documentation**: While code is clean, more architectural documentation upfront would help onboarding.

-   **Complete task early**: While we did manage to finish everything up in time, we plan to finish even earlier for upcoming iterations

  



  

## Plans for Iteration 2

  

### Priority Changes:

1.  **Implement Real Persistence**: Replace stub repositories with Room database to support actual data storage and retrieval.

2.  **Complete Workout Builder**: Implement the core workout generation algorithm that takes equipment, muscle groups, and duration as inputs.

3.  **Active Session Runner**: Build the workout execution screen that displays current exercise, manages timer, and records progress.

4.  **UI Enhancement**: Polish the Timer UI and add session history display
  

  

## Next Steps

  

1.  **Schedule Iteration 2 planning session** to refine scope and timeline

2.  **Begin database design** for persistence layer

3.  **Start research on workout generation algorithm** (complexity analysis)

4.  **Identify any blocking technical dependencies** for next iteration

  

---

  