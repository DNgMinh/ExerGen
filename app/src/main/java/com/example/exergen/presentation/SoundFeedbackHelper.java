package com.example.exergen.presentation;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class SoundFeedbackHelper {
    private final ToneGenerator toneGenerator;

    public SoundFeedbackHelper() {
        this.toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    }

    public void playCountdownBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
    }

    public void playTransitionBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 500);
    }

    public void release() {
        toneGenerator.release();
    }
}
