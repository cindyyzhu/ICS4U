package tasks;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GatheringTask extends Tasks {
    private HashMap <Image, Point> materialMap = new HashMap <Image, Point> ();

    public GatheringTask() {
        try { materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/wood.png").getImage(), new Point(200, 400));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/sinew.png").getImage(), new Point(300, 200));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/sealskin.png").getImage(), new Point(400, 300));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/copper.png").getImage(), new Point(500, 400));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/bone.png").getImage(), new Point(600, 200));

        } catch (Exception e) {
        }}

    public String gatheringMouseHandler (int x, int y, int PLAYER_SIZE) {
        for (Image material : materialMap.keySet()) { //for each material
            Point location = materialMap.get(material); //get the location of the material
            if (x >= location.x && x <= location.x + PLAYER_SIZE +30 && y >= location.y && y <= location.y + PLAYER_SIZE+30) { //if the player is at the material
               gatherScore++; //increase the gather score
                materialMap.remove(material); //remove the material
                if (gatherScore == 5) { //if the gather score is 5
                    inGatheringTask = false; //sets that the task has been completed
                    return "C2";
                }
                break;
            }
        }
        return "";
    }

    public void drawGatheringTask (Graphics g, int taskTimer, int PLAYER_SIZE, int TASK_TIME_LIMIT){
        g.drawString("Gather materials", 10, 20);
        g.drawString("Score: " + gatherScore, 10, 40);
        g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
        g.setColor(Color.ORANGE);

        for (Image material : materialMap.keySet()) {
            Point location = materialMap.get(material);
            g.drawImage(material, location.x, location.y, PLAYER_SIZE + 30, PLAYER_SIZE + 30, null);
        }}
    public void startGatheringTask () {
        inGatheringTask = true;
        gatherScore = 0;

        JOptionPane.showMessageDialog(null, "Task: Collect 1 of each of the following - wood, braided sinew, sealskin, copper rivets, and animal bone. Click on the materials to collect them.");
    }
    }

