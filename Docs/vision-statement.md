# ExerGen

## Summary

ExerGen is an offline Android application that generates structured interval workouts based on a user’s available equipment, target muscle groups, and time constraints, and then guides and records the workout from start to finish.

## Description

ExerGen is built for people who want to train but lose momentum when faced with planning decisions. In both home and gym environments, users often spend unnecessary time deciding which exercises to perform, how to structure intervals, or how to adapt when equipment availability changes. ExerGen addresses this problem by generating valid, ready-to-use workouts on demand.

At the core of the application is the Workout Builder, which creates routines using three user-defined constraints: available equipment, targeted muscle groups, and total workout duration. This allows users to generate workouts that remain appropriate whether they are in a fully equipped gym, a small apartment, or a temporary space such as a hotel gym. The generation logic ensures that only compatible exercises are selected.

Once a workout is generated, users enter an Active Session mode. During a session, the application provides a live countdown timer, displays the current exercise, previews the next exercise, and allows the user to pause or resume the workout at any time. Interval and timer configurations can be customized, enabling structured formats such as fixed work-rest cycles or longer endurance sessions.

ExerGen also supports long-term use by maintaining a complete local record of activity. Completed workouts are saved to a Session History, where users can review past sessions and their details. A Statistics Dashboard aggregates this data to show trends such as total calories burned, average workout duration, and average intensity over time. Users may also save generated routines as Workout Templates for future reuse and extend the Exercise Library by adding custom exercises.

All functionality is available offline. The application stores all data locally, ensuring reliability in environments with limited or unreliable internet access.

## Target Audience

ExerGen is intended for students, intermediate gym-goers, and home fitness users who want structure without committing to rigid, pre-written programs. It is especially suited to users who train in multiple environments and need workouts that adapt to changing equipment and space without manual reconfiguration.

## Value Proposition

ExerGen reduces planning overhead so users can focus on completing their workouts. By automatically handling exercise selection, interval timing, and session tracking, the application replaces ad hoc planning methods such as notes or memory. It offers structured guidance comparable to a training plan while preserving flexibility and control for the user.

## Success Criteria

The project will be considered successful if the following conditions are met:

- **Generation Speed**: A valid workout must be generated from three user constraints (time, equipment, muscle group) in under five seconds.
- **Filter Accuracy**: Exercise selection must match equipment constraints with 100 percent accuracy.
- **Session Continuity**: Active workout timers must remain accurate within one second when paused, resumed, or when the device orientation changes.
- **Data Integrity**: Completed workouts must be saved correctly and reflected immediately in session history and statistics in all cases.
