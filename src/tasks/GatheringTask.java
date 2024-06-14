package tasks;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GatheringTask extends Tasks {
    public HashMap <Image, Point> materialMap = new HashMap <> ();

    //hashmap to store the materials and their locations
    private void initializeMaterialMap () {
        materialMap.clear();
        materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/wood.png").getImage(), new Point(200, 400));
        materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/sinew.png").getImage(), new Point(300, 200));
        materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/sealskin.png").getImage(), new Point(400, 300));
        materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/copper.png").getImage(), new Point(500, 400));
        materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/bone.png").getImage(), new Point(600, 200));
    }
    public void drawGatheringTask (Graphics g, int taskTimer, int playerSize, int taskTimeLimit){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 250, 100); //white rectangle to see the black text on the background

        g.setColor(Color.BLACK);
        g.drawString("TASK: Gather materials needed for a bow!", 10, 20);
        g.drawString("Score: " + gatherScore, 10, 40);
        g.drawString("Time remaining: " + (taskTimeLimit - taskTimer) + " seconds", 10, 60);
        g.setColor(Color.RED);

        for (Image material : materialMap.keySet()) { //for each material, it will draw it
            Point location = materialMap.get(material);
            g.drawImage(material, location.x, location.y, playerSize + 30, playerSize + 30, null);
        }}
    public void startGatheringTask () { //starts the gathering task
        inGatheringTask = true;
        gatherScore = 0;
        initializeMaterialMap();
    }

    public String gatheringMouseHandler (int x, int y, int playerSize) {
        for (Image material : materialMap.keySet()) { //for each material
            Point location = materialMap.get(material); //get the location of the material
            if (x >= location.x && x <= location.x + playerSize +40 && y >= location.y && y <= location.y + playerSize+40) { //if the player is at the material
                gatherScore++; //increase the gather score
                materialMap.remove(material); //remove the material
                if (gatherScore == 5) { //if the gather score is 5
                    inGatheringTask = false; //sets that the task has been completed
                    return "C2";
                }
                break;
            }
        }
        return ""; //task not complete
    }
    }


