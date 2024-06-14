package tasks;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.*;

public class HuntingTask extends Tasks{
    public java.util.List<Point> animals = new ArrayList<>(); //list of animals
    public Timer animalMovementTimer; //timer for the animal movement
    public Image beaverIcon; //image of the beaver

    public HuntingTask () {
        try {//initialize the beaver icon
            beaverIcon = new ImageIcon(System.getProperty("user.dir") + "/resources/beaver.png").getImage();
        }
        catch (Exception e) {

        }
    }
    //draw the hunting task
    public void drawHuntingTask (Graphics g, int taskTimer, int player_size, int taskTimeLimit){

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 200, 100);

        g.setColor(Color.BLACK);
        g.drawString("TASK: Hunt 5 beavers!", 10, 20);
        g.drawString("Score: " + huntScore, 10, 40);
        g.drawString("Time remaining: " + (taskTimeLimit - taskTimer) + " seconds", 10, 60);

        g.setColor(Color.RED);

        for (Point animal : animals) { //for each animal, it will draw it
            if (beaverIcon != null) {
                g.drawImage(beaverIcon, animal.x, animal.y, player_size+30, player_size+30, null);
            }
        }
    }

    public String huntingMouseHandler (int x, int y, int player_size) {
        for (Point animal : animals) { //for each animal, point represents the location
            if (x >= animal.x && x <= animal.x + player_size +30 && y >= animal.y && y <= animal.y + player_size +30) { //if the player is at the animal
                huntScore++; //increase the hunt score
                animals.remove(animal); //remove the animal
                if (huntScore == 5) { //if the hunt score is 5
                    inHuntingTask = false; //sets that the task has been completed
                    return "C3";
                }
                break;
            }
        }
        return ""; //task not complete
    }
    public void startHuntingTask(int windowWidth, int windowHeight) {
        inHuntingTask = true;
        huntScore = 0;
        animals.clear(); //clear the list of animals
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            animals.add(new Point(random.nextInt(windowWidth - 50), random.nextInt(windowHeight - 50))); //add 5 animals to the list
        }

        animalMovementTimer = new Timer(400, new ActionListener() { //allows it to move, decreasing the delay will move it faster
            @Override
            public void actionPerformed(ActionEvent e) {
                moveAnimals(windowWidth, windowHeight);
            }
        });
        animalMovementTimer.start();
    }

    public void moveAnimals(int windowWidth, int windowHeight) { //moves the animals
        Random random = new Random();
        for (Point animal : animals) {
            animal.x = Math.max(0, Math.min(animal.x + random.nextInt(41) - 10, windowWidth - 100));
            animal.y = Math.max(0, Math.min(animal.y + random.nextInt(41) - 10, windowHeight - 100));
        }
    }
}
