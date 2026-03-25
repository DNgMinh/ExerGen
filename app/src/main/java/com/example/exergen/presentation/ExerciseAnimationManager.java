package com.example.exergen.presentation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExerciseAnimationManager {
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private final List<Drawable> frames = new ArrayList<>();
    private final ImageView imageView;
    private final String logTag;
    private int currentFrame = 0;
    private boolean isAnimating = false;

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (frames.isEmpty() || !isAnimating) {
                return;
            }
            currentFrame = (currentFrame + 1) % frames.size();
            imageView.setImageDrawable(frames.get(currentFrame));
            animationHandler.postDelayed(this, 1000);
        }
    };

    public ExerciseAnimationManager(ImageView imageView, String logTag) {
        if (imageView == null) {
            throw new IllegalArgumentException("imageView required");
        }
        this.imageView = imageView;
        this.logTag = logTag == null ? "ExerciseAnimationManager" : logTag;
    }

    public void loadAndStart(Context context, List<String> paths) {
        stop();
        frames.clear();
        if (context == null || paths == null) {
            return;
        }

        for (String path : paths) {
            try (InputStream is = context.getAssets().open(path)) {
                Drawable drawable = Drawable.createFromStream(is, null);
                if (drawable != null) {
                    frames.add(drawable);
                }
            } catch (IOException e) {
                Log.e(logTag, "Error loading image: " + path, e);
            }
        }

        if (!frames.isEmpty()) {
            isAnimating = true;
            currentFrame = 0;
            imageView.setImageDrawable(frames.get(0));
            animationHandler.post(animationRunnable);
        }
    }

    public void pause() {
        isAnimating = false;
        animationHandler.removeCallbacks(animationRunnable);
    }

    public void resume() {
        if (!isAnimating && !frames.isEmpty()) {
            isAnimating = true;
            animationHandler.post(animationRunnable);
        }
    }

    public void stop() {
        isAnimating = false;
        animationHandler.removeCallbacks(animationRunnable);
        frames.clear();
    }
}
