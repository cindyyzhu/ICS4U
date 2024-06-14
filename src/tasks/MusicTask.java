package tasks;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;

public class MusicTask extends Tasks {
    //intializing constructor values
    private int noteIndex = 0;
    private final String[] notes = {"a", "b", "c", "d", "e", "f", "g"};
    private final char[] noteKeys = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};

    private Image musicNotes;

    public MusicTask() {
        try { //initialize the music notes background picture
            musicNotes = new ImageIcon(System.getProperty("user.dir") + "/resources/MusicalNotes.png").getImage();

        } catch (Exception e) {

        }
    }

    public void drawMusicTask(Graphics g) { //text on screen
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 280, 100); //highlighting the text

        g.setColor(Color.BLACK);
        g.drawString("TASK: Play the notes in order: a, b, c, d, e, f, g", 10, 20);

        if (musicNotes != null) { //draws the image background
            g.drawImage(musicNotes, 800 / 2 - 300, 600/ 2 - 170, 600, 350, null);
        }

        g.setColor(Color.BLUE);
        g.drawOval(233 + noteIndex * 69, 415, 50, 50); //this will draw the ovals to indicate the current note to be played next

        g.setColor(Color.RED);
    }

    public void startMusicTask() { //starts it
        inMusicTask = true;
        noteIndex = 0;
    }

    public void play(char note) {
        try {
            // audio file
            File f = new File(System.getProperty("user.dir") + "/resources/" + note + ".wav");

            // load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);

            // create a clip for the audio
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            // wait for the clip to finish playing
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
        if (inMusicTask){
            char expectedNote = noteKeys[noteIndex];
            if (keyPressed == expectedNote) {
                play(keyPressed); // play the sound for the correct note
                noteIndex++;
                if (noteIndex < notes.length) {
                    // continue to the next note, do nothing
                } else {
                    inMusicTask = false;
                    return "C1";
                }
            } else if (keyPressed != expectedNote) {
                JOptionPane.showMessageDialog(null, "You played the wrong note! Try again.");
                noteIndex = 0;
                return "WrongNote";
            }

        }
        return ""; //incomplete task
    }
}



