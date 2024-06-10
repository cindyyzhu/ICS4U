package tasks;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.*;

public class HuntingTask extends Tasks{
    private java.util.List<Point> animals = new ArrayList<>();
    public Timer animalMovementTimer;

    public Image beaverIcon;

    public HuntingTask () {
        try {
            beaverIcon = new ImageIcon(System.getProperty("user.dir") + "/resources/beaver.png").getImage();
        }
        catch (Exception e) {
        }
    }


    public void drawHuntingTask (Graphics g, int taskTimer, int PLAYER_SIZE, int TASK_TIME_LIMIT){

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 200, 100);

        g.setColor(Color.BLACK);
        g.drawString("TASK: Hunt 5 beavers!", 10, 20);
        g.drawString("Score: " + huntScore, 10, 40);
        g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
        g.setColor(Color.RED);

        for (Point animal : animals) {
            if (beaverIcon != null) {
                g.drawImage(beaverIcon, animal.x, animal.y, PLAYER_SIZE+30, PLAYER_SIZE+30, null);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(animal.x, animal.y, PLAYER_SIZE, PLAYER_SIZE);
            }
        }
    }

    public String huntingMouseHandler (int x, int y, int PLAYER_SIZE) {
        for (Point animal : animals) { //for each animal, point represents the location
            if (x >= animal.x && x <= animal.x + PLAYER_SIZE +30 && y >= animal.y && y <= animal.y + PLAYER_SIZE +30) { //if the player is at the animal
                huntScore++; //increase the hunt score
                animals.remove(animal); //remove the animal
                if (huntScore == 5) { //if the hunt score is 5
                    inHuntingTask = false; //sets that the task has been completed
                    return "C3";
                }
                break;
            }
        }
        return "";
    }
    public void startHuntingTask(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
        inHuntingTask = true;
        huntScore = 0;
        animals.clear();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            animals.add(new Point(random.nextInt(WINDOW_WIDTH - 50), random.nextInt(WINDOW_HEIGHT - 50)));
        }
        JOptionPane.showMessageDialog(null, "Task: Hunt 5 beavers. Click on the moving beavers to hunt them.");

        animalMovementTimer = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveAnimals(WINDOW_WIDTH, WINDOW_HEIGHT);
            }
        });
        animalMovementTimer.start();
    }

    public void moveAnimals(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
        Random random = new Random();
        for (Point animal : animals) {
            animal.x = Math.max(0, Math.min(animal.x + random.nextInt(41) - 10, WINDOW_WIDTH - 100));
            animal.y = Math.max(0, Math.min(animal.y + random.nextInt(41) - 10, WINDOW_HEIGHT - 100));
        }
    }
}
