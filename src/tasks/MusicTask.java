package tasks;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;

public class MusicTask extends Tasks {
    private int noteIndex = 0;
    private final String[] notes = {"a", "b", "c", "d", "e", "f", "g"};
    private final char[] noteKeys = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};

    private Image musicNotes;

    public MusicTask() {
        try {
            musicNotes = new ImageIcon(System.getProperty("user.dir") + "/resources/MusicalNotes.png").getImage();
        } catch (Exception e) {

        }
    }

    public void drawMusicTask(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 250, 100);
        g.setColor(Color.BLACK);
        g.drawString("Play the notes in order: a, b, c, d, e, f, g", 10, 20);
        g.setColor (Color.WHITE);

        if (musicNotes != null) {
            g.drawImage(musicNotes, 800 / 2 - 300, 600/ 2 - 170, 600, 350, null);
        }
        g.drawOval(233 + noteIndex * 69, 415, 50, 50);
    }

    public void startMusicTask() {
        inMusicTask = true;
        musicScore = 0;
        noteIndex = 0;
        JOptionPane.showMessageDialog(null, "Task: Play music to satisfy your elder in order to learn more about the history of fur trades.\nPress the keys in the order: a, b, c, d, e, f, g.");
    }

    public void play(char note) {
        try {
            // Construct the file path to the note's audio file
            File f = new File(System.getProperty("user.dir") + "/resources/" + note + ".wav");

            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);

            // Create a clip to play the audio
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            // Wait for the clip to finish playing
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        event.getLine().close();
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    public String handleMusicTask(char keyPressed) {
        if (inMusicTask && new String(noteKeys).indexOf(keyPressed) != -1) {

            char expectedNote = noteKeys[noteIndex];
            if (keyPressed == expectedNote) {
                play(keyPressed); // Play the sound for the correct note
                musicScore++;
                noteIndex++;
                if (noteIndex < notes.length) {
                    // Continue to the next note
                } else {
                    inMusicTask = false;
                    return "C1";
                }
            } else {
                JOptionPane.showMessageDialog(null, "You played the wrong note! Try again.");
                musicScore--;
                noteIndex = 0;

            }

        }
        return "";
    }
}



