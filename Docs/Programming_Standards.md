GPA = GPT

Programming Standards

**Naming Conventions**

- Variables and Functions: We will follow our usual ‘lowerCamelCase’. Also, we will make our function name declarative e.g., calculateWorkoutDuration() instead of just calc().
- Classes and Interfaces: We will follow ‘UpperCamelCase’ like ‘KalpIsGreat’ (of course he is).
- Constant values: All Caps like ’CALORIE\_COUNT’
- Layout Files (XML): We will use ‘snake\_case’ e.g. activity\_main.xml

**Git and Branching** 

- Branch naming: feature/issue#-short-description (e.g. feature/12-stub-database)
- Commit messages: To the point and reference issue number e.g. Add unit tests for WorkourBuilder #15
- Merging: Require at least one Peer Review i.e. should be reviewed by at least one other person other than who is sending the merge request.

**Architecture**

- Strictly forbid UI classes from accessing Database (Stub) directly. All data must pass through Logic/Domain layer.
- Packages should be organized by layer or feature (e.g., com.exergen.persistence, com.exergen.ui)



*Add other programming standards here*









**A sample Git Workflow**

1) Pull latest changes

   `		`git pull origin main

1) ` `Create a branch: Name is based on the issueID from GitLab

   `		`Format: feature/issue#-description

   `		`Example: git checkout -b feature/12-stub-workout

1) To the point commits

   `	`Example: git commit -m “Implement getExercise in Stub #12”

1) Before merging to main make sure:

`	`No dead code: Remove unused imports or unnecessary 				comments

`	`No unfinished TODOs: Ensure everything is complete

`	`Unit Test Pass: Must have a thorough set of unit tests 					for complex logic

`	`No Warnings: Check for any warnings in Android 					Studio that might throw

1) Finally Merge Request:

   `	`Open a Merge Request: To merge with ‘main’ branch

   `	`Peer Review: One other teammate(atleast) should 		review the code before it is merged.

   `	`Record: Update the time and status of that feature on 	GitLab 	

Sample Code Snippet

`	`Look at the comments, brackets, etc.

package com.exergen.logic; // Package matches architectural layer 

import com.exergen.persistence.WorkoutPersistence;

import java.util.List;

/\*\*

` `\* Handles the generation logic for workouts based on constraints.

` `\* This class represents the "Logic Layer."

` `\*/

public class WorkoutGenerator {

`    `private final WorkoutPersistence persistence;

`    `// Constant follows UPPER\_SNAKE\_CASE

`    `private static final int MAX\_GENERATION\_TIME\_SECONDS = 5;

`    `public WorkoutGenerator(WorkoutPersistence persistence) {

`        `this.persistence = persistence;

`    `}

`    `/\*\*

`     `\* Generates a workout based on user constraints.

`     `\* Logic follows the "Success Criteria" in the Vision Statement.

`     `\*/

`    `public Workout buildWorkout(List<String> equipment, List<String> muscles, int duration) {

`        `// Variable follows lowerCamelCase

`        `Workout newWorkout = new Workout(duration);

`        `// TODO: Implement the filtering algorithm for equipment accuracy [cite: 229]

`        `// Example of "interesting" logic required for Iteration 1 [cite: 219]



`        `return newWorkout;

`    `}

}

