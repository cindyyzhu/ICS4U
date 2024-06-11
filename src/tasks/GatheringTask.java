package tasks;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GatheringTask extends Tasks {
    public HashMap <Image, Point> materialMap = new HashMap <> ();

   public GatheringTask() {
        try { materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/wood.png").getImage(), new Point(200, 400));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/sinew.png").getImage(), new Point(300, 200));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/sealskin.png").getImage(), new Point(400, 300));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/copper.png").getImage(), new Point(500, 400));
            materialMap.put(new ImageIcon(System.getProperty("user.dir") + "/resources/bone.png").getImage(), new Point(600, 200));

        } catch (Exception e) {
            e.printStackTrace();
        }}

    public void drawGatheringTask (Graphics g, int taskTimer, int PLAYER_SIZE, int TASK_TIME_LIMIT){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 250, 100); //white rectangle to see the black text on the background

        g.setColor(Color.BLACK);
        g.drawString("TASK: Gather materials needed for a bow!", 10, 20);
        g.drawString("Score: " + gatherScore, 10, 40);
        g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
        g.setColor(Color.RED);

        for (Image material : materialMap.keySet()) {
            Point location = materialMap.get(material);
            g.drawImage(material, location.x, location.y, PLAYER_SIZE + 30, PLAYER_SIZE + 30, null);
        }}
    public void startGatheringTask () {
        inGatheringTask = true;
        gatherScore = 0;

        JOptionPane.showMessageDialog(null, "Task: Collect 1 of each of the following - wood, braided sinew, sealskin, copper rivets, and animal bone. Click on the materials to collect them.");
    }

    public String gatheringMouseHandler (int x, int y, int PLAYER_SIZE) {
        for (Image material : materialMap.keySet()) { //for each material
            Point location = materialMap.get(material); //get the location of the material
            if (x >= location.x && x <= location.x + PLAYER_SIZE +40 && y >= location.y && y <= location.y + PLAYER_SIZE+40) { //if the player is at the material
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
    }


