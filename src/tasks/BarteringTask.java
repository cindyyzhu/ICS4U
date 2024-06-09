package tasks;

import javax.swing.*;
import java.util.Random;
import java.awt.*;

public class BarteringTask extends Tasks {
    public void drawBarteringTask (Graphics g, int taskTimer, int TASK_TIME_LIMIT){
        g.drawString("Barter with the trading staff", 10, 20);
        g.drawString("Score: " + barterScore, 10, 40);
        g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
    }

    public String startBarteringTask(int failureCount) {
        inBarteringTask = true;
        barterScore = 0;

        Random random = new Random();
        int minPrice = random.nextInt(10) + 1;
        int maxPrice = minPrice + random.nextInt(6) + 1;
        JOptionPane.showMessageDialog(null, "Task: Make a successful trade. Enter a price to sell your beaver furs.");

        boolean successfulTrade = false;

        while(!successfulTrade) {
            String input = JOptionPane.showInputDialog("Enter a price to sell your beaver furs (1-10):");
            int price = Integer.parseInt(input);
            if (price >= minPrice && price <= maxPrice) {
                JOptionPane.showMessageDialog(null, "Successful trade! The trader was willing to buy the furs for " + minPrice + " - " + maxPrice + " dollars.");
                barterScore = 1;
                inBarteringTask = false;
                successfulTrade = true;
                return "C4";
            } else {
                failureCount++;
                if (price < minPrice) {
                    JOptionPane.showMessageDialog(null, "Trader: \"It's obviously not good quality if it's that cheap.\"");
                } else {
                    JOptionPane.showMessageDialog(null, "Trader: \"So expensive! How arrogant!\"");
                }}
        }
        return "";

    }


}
