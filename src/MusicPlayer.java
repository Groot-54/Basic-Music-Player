import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer extends JFrame {
    private AudioPlayer audioPlayer;
    private JLabel songLabel, timeLabel;
    private JButton playButton, pauseButton, stopButton, loadButton;
    private JSlider volumeSlider, progressSlider;
    private Timer timer;
    private File currentFile;

    public MusicPlayer() {
        audioPlayer=new AudioPlayer();
        initializeGUI();
        setupEventListener();
        startProgressTimer();
    }

    private void initializeGUI() {
        setTitle("First Music Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        songLabel = new JLabel("No song loaded", JLabel.CENTER);
        songLabel.setFont(new Font("Ariel", Font.BOLD, 16));
        songLabel.setBorder(BorderFactory.createEtchedBorder());
        topPanel.add(songLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBorder(BorderFactory.createTitledBorder("Progress"));
        centerPanel.add(progressSlider, BorderLayout.CENTER);

        timeLabel = new JLabel("00:00 / 00:00", JLabel.CENTER);
        centerPanel.add(timeLabel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout());

        loadButton = new JButton("Load Song");
        loadButton.setPreferredSize(new Dimension(100, 40));
        loadButton.setBackground(new Color(70,130, 180));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);

        playButton = new JButton("Play");
        playButton.setPreferredSize(new Dimension(80, 40));
        playButton.setBackground(new Color(34,139,34));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setEnabled(false);

        pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(80, 40));
        pauseButton.setBackground(new Color(255, 140, 0));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setEnabled(false);

        stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(80, 40));
        stopButton.setBackground(new Color(255, 140, 0));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);

        buttonPanel.add(loadButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel volumePanel = new JPanel(new FlowLayout());
        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setBorder(BorderFactory.createTitledBorder("Volume"));
        volumeSlider.setPreferredSize(new Dimension(150, 50));
        volumePanel.add(volumeSlider);

        bottomPanel.add(volumePanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventListener() {
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAudioFile();
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                audioPlayer.play();
                updateButtonStates();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                audioPlayer.pause();
                updateButtonStates();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                audioPlayer.stop();
                progressSlider.setValue(0);
                updateButtonStates();
            }
        });

        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100.0f;
            audioPlayer.setVolume(volume);
        });

        progressSlider.addChangeListener(e -> {
            if (progressSlider.getValueIsAdjusting()) {
                long totalDuration = audioPlayer.getTotalDuration();
                long newPosition = (long) ((progressSlider.getValue() / 100.0) * totalDuration);
                audioPlayer.setPosition(newPosition);
            }
        });
    }
    private void loadAudioFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Audio File");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Audio File", "wav", "aiff", "au");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                audioPlayer.loadAudio(currentFile);
                songLabel.setText(currentFile.getName());
                progressSlider.setValue(0);
                updateButtonStates();

                JOptionPane.showMessageDialog(this,
                        "Audio file loaded successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading audio file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateButtonStates() {
        boolean hasAudio = currentFile != null;
        boolean isPlaying = audioPlayer.isPlaying();
        boolean isPaused = audioPlayer.isPaused();

        playButton.setEnabled(hasAudio && (!isPlaying || isPaused));
        pauseButton.setEnabled(hasAudio && !isPlaying);
        stopButton.setEnabled(hasAudio && (isPlaying || isPaused));
    }

    private void startProgressTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    updateProgress();
                    updateButtonStates();
                });
            }
        }, 0, 1000);
    }

    private void updateProgress() {
        if (currentFile != null) {
            long currentPos = audioPlayer.getCurrentPosition();
            long totalDuration = audioPlayer.getTotalDuration();

            if (totalDuration > 0) {
                int progress = (int) ((currentPos * 100) / totalDuration);
                if (!progressSlider.getValueIsAdjusting()) {
                    progressSlider.setValue(progress);
                }

                String currentTime = formatTime(currentPos);
                String totalTime = formatTime(totalDuration);
                timeLabel.setText(currentTime + " / " + totalTime);
            }
        }
    }

    private String formatTime(long microseconds) {
        long seconds = microseconds / 1_000_000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.cancel();
        }
        audioPlayer.close();
        super.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MusicPlayer().setVisible(true);
        });
    }
}
