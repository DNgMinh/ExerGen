package com.example.exergen.persistence;

import com.example.exergen.persistence.repository.IExerciseRepository;
import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseRepositoryStub implements IExerciseRepository {
    private final List<Exercise> exercises = new ArrayList<>();

    public ExerciseRepositoryStub() {
        exercises.add(new Exercise(
                "ex_1", "Pushups", List.of(MuscleGroup.CHEST), List.of(EquipmentType.BODYWEIGHT),
                "Lie on the floor face down and place your hands about 36 inches apart while holding your torso up at arms length. Next, lower yourself downward until your chest almost touches the floor as you inhale. Now breathe out and press your upper body back up to the starting position while squeezing your chest. After a brief pause at the top contracted position, you can begin to lower yourself downward again for as many repetitions as needed.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_2", "Dumbbell Bench Press", List.of(MuscleGroup.CHEST), List.of(EquipmentType.DUMBBELLS),
                "Lie down on a flat bench with a dumbbell in each hand resting on top of your thighs. The palms of your hands will be facing each other. Then, using your thighs to help raise the dumbbells up, lift the dumbbells one at a time so that you can hold them in front of you at shoulder width. Once at shoulder width, rotate your wrists forward so that the palms of your hands are facing away from you. The dumbbells should be just to the sides of your chest, with your upper arm and forearm creating a 90 degree angle. Be sure to maintain full control of the dumbbells at all times. This will be your starting position. Then, as you breathe out, use your chest to push the dumbbells up. Lock your arms at the top of the lift and squeeze your chest, hold for a second and then begin coming down slowly. Tip: Ideally, lowering the weight should take about twice as long as raising it. Repeat the movement for the prescribed amount of repetitions of your training program.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_3", "Dumbbell Flyes", List.of(MuscleGroup.CHEST), List.of(EquipmentType.DUMBBELLS),
                "Lie down on a flat bench with a dumbbell on each hand resting on top of your thighs. The palms of your hand will be facing each other. Then using your thighs to help raise the dumbbells, lift the dumbbells one at a time so you can hold them in front of you at shoulder width with the palms of your hands facing each other. Raise the dumbbells up like you're pressing them, but stop and hold just before you lock out. This will be your starting position. With a slight bend on your elbows in order to prevent stress at the biceps tendon, lower your arms out at both sides in a wide arc until you feel a stretch on your chest. Breathe in as you perform this portion of the movement. Tip: Keep in mind that throughout the movement, the arms should remain stationary; the movement should only occur at the shoulder joint. Return your arms back to the starting position as you squeeze your chest muscles and breathe out. Tip: Make sure to use the same arc of motion used to lower the weights. Hold for a second at the contracted position and repeat the movement for the prescribed amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_4", "Bent Over Barbell Row", List.of(MuscleGroup.BACK), List.of(EquipmentType.BARBELL),
                "Holding a barbell with a pronated grip (palms facing down), bend your knees slightly and bring your torso forward, by bending at the waist, while keeping the back straight until it is almost parallel to the floor. Tip: Make sure that you keep the head up. The barbell should hang directly in front of you as your arms hang perpendicular to the floor and your torso. This is your starting position. Now, while keeping the torso stationary, breathe out and lift the barbell to you. Keep the elbows close to the body and only use the forearms to hold the weight. At the top contracted position, squeeze the back muscles and hold for a brief pause. Then inhale and slowly lower the barbell back to the starting position. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_5", "Barbell Shoulder Press", List.of(MuscleGroup.SHOULDERS), List.of(EquipmentType.BARBELL),
                "Sit on a bench with back support in a squat rack. Position a barbell at a height that is just above your head. Grab the barbell with a pronated grip (palms facing forward). Once you pick up the barbell with the correct grip width, lift the bar up over your head by locking your arms. Hold at about shoulder level and slightly in front of your head. This is your starting position. Lower the bar down to the shoulders slowly as you inhale. Lift the bar back up to the starting position as you exhale. Repeat for the recommended amount of repetitions.",
                3, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_6", "Dumbbell Shoulder Press", List.of(MuscleGroup.SHOULDERS), List.of(EquipmentType.DUMBBELLS),
                "While holding a dumbbell in each hand, sit on a military press bench or utility bench that has back support. Place the dumbbells upright on top of your thighs. Now raise the dumbbells to shoulder height one at a time using your thighs to help propel them up into position. Make sure to rotate your wrists so that the palms of your hands are facing forward. This is your starting position. Now, exhale and push the dumbbells upward until they touch at the top. Then, after a brief pause at the top contracted position, slowly lower the weights back down to the starting position while inhaling. Repeat for the recommended amount of repetitions.",
                3, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_7", "Side Lateral Raise", List.of(MuscleGroup.SHOULDERS), List.of(EquipmentType.DUMBBELLS),
                "Pick a couple of dumbbells and stand with a straight torso and the dumbbells by your side at arms length with the palms of the hand facing you. This will be your starting position. While maintaining the torso in a stationary position (no swinging), lift the dumbbells to your side with a slight bend on the elbow and the hands slightly tilted forward as if pouring water in a glass. Continue to go up until you arms are parallel to the floor. Exhale as you execute this movement and pause for a second at the top. Lower the dumbbells back down slowly to the starting position as you inhale. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_8", "Front Dumbbell Raise", List.of(MuscleGroup.SHOULDERS), List.of(EquipmentType.DUMBBELLS),
                "Pick a couple of dumbbells and stand with a straight torso and the dumbbells on front of your thighs at arms length with the palms of the hand facing your thighs. This will be your starting position. While maintaining the torso stationary (no swinging), lift the left dumbbell to the front with a slight bend on the elbow and the palms of the hands always facing down. Continue to go up until you arm is slightly above parallel to the floor. Exhale as you execute this portion of the movement and pause for a second at the top. Inhale after the second pause. Now lower the dumbbell back down slowly to the starting position as you simultaneously lift the right dumbbell. Continue alternating in this fashion until all of the recommended amount of repetitions have been performed for each arm.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_9", "Barbell Curl", List.of(MuscleGroup.BICEPS), List.of(EquipmentType.BARBELL),
                "Stand up with your torso upright while holding a barbell at a shoulder-width grip. The palm of your hands should be facing forward and the elbows should be close to the torso. This will be your starting position. While holding the upper arms stationary, curl the weights forward while contracting the biceps as you breathe out. Tip: Only the forearms should move. Continue the movement until your biceps are fully contracted and the bar is at shoulder level. Hold the contracted position for a second and squeeze the biceps hard. Slowly begin to bring the bar back to starting position as your breathe in. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_10", "Hammer Curls", List.of(MuscleGroup.BICEPS), List.of(EquipmentType.DUMBBELLS),
                "Stand up with your torso upright and a dumbbell on each hand being held at arms length. The elbows should be close to the torso. The palms of the hands should be facing your torso. This will be your starting position. Now, while holding your upper arm stationary, exhale and curl the weight forward while contracting the biceps. Continue to raise the weight until the biceps are fully contracted and the dumbbell is at shoulder level. Hold the contracted position for a brief moment as you squeeze the biceps. Tip: Focus on keeping the elbow stationary and only moving your forearm. After the brief pause, inhale and slowly begin the lower the dumbbells back down to the starting position. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_11", "Concentration Curls", List.of(MuscleGroup.BICEPS), List.of(EquipmentType.DUMBBELLS),
                "Sit down on a flat bench with one dumbbell in front of you between your legs. Your legs should be spread with your knees bent and feet on the floor. Use your right arm to pick the dumbbell up. Place the back of your right upper arm on the top of your inner right thigh. Rotate the palm of your hand until it is facing forward away from your thigh. Tip: Your arm should be extended and the dumbbell should be above the floor. This will be your starting position. While holding the upper arm stationary, curl the weights forward while contracting the biceps as you breathe out. Only the forearms should move. Continue the movement until your biceps are fully contracted and the dumbbells are at shoulder level. Tip: At the top of the movement make sure that the little finger of your arm is higher than your thumb. This guarantees a good contraction. Hold the contracted position for a second as you squeeze the biceps. Slowly begin to bring the dumbbells back to starting position as your breathe in. Caution: Avoid swinging motions at any time. Repeat for the recommended amount of repetitions. Then repeat the movement with the left arm.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_12", "Incline Dumbbell Curl", List.of(MuscleGroup.BICEPS), List.of(EquipmentType.DUMBBELLS),
                "Sit back on an incline bench with a dumbbell in each hand held at arms length. Keep your elbows close to your torso and rotate the palms of your hands until they are facing forward. This will be your starting position. While holding the upper arm stationary, curl the weights forward while contracting the biceps as you breathe out. Only the forearms should move. Continue the movement until your biceps are fully contracted and the dumbbells are at shoulder level. Hold the contracted position for a second. Slowly begin to bring the dumbbells back to starting position as your breathe in. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_13", "Dumbbell Lunges", List.of(MuscleGroup.LEGS), List.of(EquipmentType.DUMBBELLS),
                "Stand with your torso upright holding two dumbbells in your hands by your sides. This will be your starting position. Step forward with your right leg around 2 feet or so from the foot being left stationary behind and lower your upper body down, while keeping the torso upright and maintaining balance. Inhale as you go down. Note: As in the other exercises, do not allow your knee to go forward beyond your toes as you come down, as this will put undue stress on the knee joint. Make sure that you keep your front shin perpendicular to the ground. Using mainly the heel of your foot, push up and go back to the starting position as you exhale. Repeat the movement for the recommended amount of repetitions and then perform with the left leg.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_14", "Triceps Pushdown", List.of(MuscleGroup.TRICEPS), List.of(EquipmentType.CABLE),
                "Attach a straight or angled bar to a high pulley and grab with an overhand grip (palms facing down) at shoulder width. Standing upright with the torso straight and a very small inclination forward, bring the upper arms close to your body and perpendicular to the floor. The forearms should be pointing up towards the pulley as they hold the bar. This is your starting position. Using the triceps, bring the bar down until it touches the front of your thighs and the arms are fully extended perpendicular to the floor. The upper arms should always remain stationary next to your torso and only the forearms should move. Exhale as you perform this movement. After a second hold at the contracted position, bring the bar slowly up to the starting point. Breathe in as you perform this step. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_15", "Close-Grip Barbell Bench Press", List.of(MuscleGroup.TRICEPS), List.of(EquipmentType.BARBELL),
                "Lie back on a flat bench. Using a close grip (around shoulder width), lift the bar from the rack and hold it straight over you with your arms locked. This will be your starting position. As you breathe in, come down slowly until you feel the bar on your middle chest. Tip: Make sure that - as opposed to a regular bench press - you keep the elbows close to the torso at all times in order to maximize triceps involvement. After a second pause, bring the bar back to the starting position as you breathe out and push the bar using your triceps muscles. Lock your arms in the contracted position, hold for a second and then start coming down slowly again. Tip: It should take at least twice as long to go down than to come up. Repeat the movement for the prescribed amount of repetitions. When you are done, place the bar back in the rack.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_16", "Dips - Triceps Version", List.of(MuscleGroup.TRICEPS), List.of(EquipmentType.BODYWEIGHT),
                "To get into the starting position, hold your body at arm's length with your arms nearly locked above the bars. Now, inhale and slowly lower yourself downward. Your torso should remain upright and your elbows should stay close to your body. This helps to better focus on tricep involvement. Lower yourself until there is a 90 degree angle formed between the upper arm and forearm. Then, exhale and push your torso back up using your triceps to bring your body back to the starting position. Repeat the movement for the prescribed amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_17", "Barbell Squat", List.of(MuscleGroup.LEGS), List.of(EquipmentType.BARBELL),
                "This exercise is best performed inside a squat rack for safety purposes. To begin, first set the bar on a rack to just below shoulder level. Once the correct height is chosen and the bar is loaded, step under the bar and place the back of your shoulders (slightly below the neck) across it. Hold on to the bar using both arms at each side and lift it off the rack by first pushing with your legs and at the same time straightening your torso. Step away from the rack and position your legs using a shoulder width medium stance with the toes slightly pointed out. Keep your head up at all times and also maintain a straight back. This will be your starting position. (Note: For the purposes of this discussion we will use the medium stance described above which targets overall development; however you can choose any of the three stances discussed in the foot stances section). Begin to slowly lower the bar by bending the knees and hips as you maintain a straight posture with the head up. Continue down until the angle between the upper leg and the calves becomes slightly less than 90-degrees. Inhale as you perform this portion of the movement. Tip: If you performed the exercise correctly, the front of the knees should make an imaginary straight line with the toes that is perpendicular to the front. If your knees are past that imaginary line (if they are past your toes) then you are placing undue stress on the knee and the exercise has been performed incorrectly. Begin to raise the bar as you exhale by pushing the floor with the heel of your foot as you straighten the legs again and go back to the starting position. Repeat for the recommended amount of repetitions.",
                2, List.of("placeholder.png")));

        exercises.add(new Exercise(
                "ex_18", "Bodyweight Squat", List.of(MuscleGroup.LEGS), List.of(EquipmentType.BODYWEIGHT),
                "Stand with your feet shoulder width apart. You can place your hands behind your head. This will be your starting position. Begin the movement by flexing your knees and hips, sitting back with your hips. Continue down to full depth if you are able,and quickly reverse the motion until you return to the starting position. As you squat, keep your head and chest up and push your knees out.",
                2, List.of("placeholder.png")));
    }

    @Override
    public Exercise getExerciseById(String id) {
        for (Exercise exercise : exercises) {
            if (exercise.getId().equals(id)) {
                return exercise;
            }
        }
        return null;
    }

    @Override
    public void deleteExercise(String id) {
        exercises.removeIf(exercise -> exercise.getId().equals(id));
    }

    @Override
    public void seedData() {
        // The stub automatically seeds data in its constructor,
        // so this method can safely remain empty.
    }

    @Override
    public List<Exercise> getAllExercises() {
        return List.copyOf(exercises);
    }

    @Override
    public void insertExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise required");
        }

        for (Exercise existing : exercises) {
            if (existing.getId().equals(exercise.getId())) {
                throw new DuplicateExerciseException(exercise.getId());
            }
        }

        exercises.add(exercise);
    }

    @Override
    public List<Exercise> filterByEquipment(EquipmentType equipment) {
        if (equipment == null) {
            throw new InvalidFilterException("Equipment filter must be non-empty.");
        }

        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : exercises) {
            for (EquipmentType currentEquipment : exercise.getEquipment()) {
                if (currentEquipment == equipment) {
                    result.add(exercise);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<Exercise> filterByMuscleGroup(MuscleGroup muscle) {
        if (muscle == null) {
            throw new InvalidFilterException("Muscle group filter must be non-empty.");
        }

        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : exercises) {
            for (MuscleGroup currentMuscle : exercise.getMuscleGroups()) {
                if (currentMuscle == muscle) {
                    result.add(exercise);
                    break;
                }
            }
        }
        return result;
    }

    public void addExercise(Exercise exercise) {
        insertExercise(exercise);
    }
}
