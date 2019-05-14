package ru.code23rus.pomodoro;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {

    public void playWorkStarted() {
        play("/sounds/work-started.wav");
    }

    public void playWorkFinished() {
        play("/sounds/work-finished.wav");
    }

    /**
     * @param repeatDelay TODO заменить этот затейливый способ сэмплирования
     */
    private void play(String resourcePath, int... repeatDelay) {
        Thread th = new Thread() {

            private void play() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
                InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
                //add buffer for mark/reset support
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(inputStream);
                clip.start();
            }

            @Override
            public void run() {
                try {
                    play();
                    for (int delay : repeatDelay) {
                        sleep(delay);
                        play();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        th.start();
    }

}
