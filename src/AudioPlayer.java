import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip audioClip;
    private AudioInputStream audioStream;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private long pausePosition = 0;

    public void loadAudio(File audioFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (audioClip != null && audioClip.isOpen()) {
            audioClip.close();
        }

        audioStream = AudioSystem.getAudioInputStream(audioFile);
        audioClip = AudioSystem.getClip();
        audioClip.open(audioStream);

        isPlaying = false;
        isPaused = false;
        pausePosition = 0;
    }

    public void play() {
        if (audioClip != null) {
            if (isPaused) {
                audioClip.setMicrosecondPosition(pausePosition);
                isPaused = false;
            }
            audioClip.start();
            isPlaying = true;
        }
    }

    public void pause() {
        if (audioClip != null && isPlaying) {
            pausePosition = audioClip.getMicrosecondPosition();
            audioClip.stop();
            isPlaying = false;
            isPaused = true;
        }
    }

    public void stop() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.setMicrosecondPosition(0);
            isPlaying = false;
            isPaused = false;
            pausePosition = 0;
        }
    }

    public void setVolume(float volume) {
        if (audioClip != null) {
            FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = volumeControl.getMaximum() - volumeControl.getMinimum();
            float gain = (range * volume) + volumeControl.getMinimum();
            volumeControl.setValue(gain);
        }
    }

    public boolean isPlaying() {
        return isPlaying && audioClip != null && audioClip.isRunning();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public long getCurrentPosition() {
        if (audioClip != null) {
            return audioClip.getMicrosecondPosition();
        }
        return 0;
    }

    public long getTotalDuration() {
        if(audioClip != null) {
            return audioClip.getMicrosecondLength();
        }
        return 0;
    }

    public void setPosition(long microseconds) {
        if(audioClip != null) {
            audioClip.setMicrosecondPosition(microseconds);
        }
    }

    public void close() {
        if (audioClip != null) {
            audioClip.close();
        }
        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
