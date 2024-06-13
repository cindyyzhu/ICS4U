package tasks;

import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.util.*;

public class BarteringTask extends Tasks {
    private ArrayList<Item> items;
    private int height = 200;
    private int width = 200;
    private Item sortAscButton;
    private Item sortDesButton;
    public BarteringTask() {
        items = new ArrayList<>();
        loadItems();

        sortAscButton = new Item("Sort Ascending", new ImageIcon(System.getProperty("user.dir") + "/resources/AscButton.png"), 0, 600, 100);
        sortDesButton = new Item("Sort Descending", new ImageIcon(System.getProperty("user.dir") + "/resources/DesButton.png"), 0,  600, 150);
    }

    private void loadItems(){
        items.clear();
        Random random = new Random();
        items.add(new Item("TeaSet", new ImageIcon(System.getProperty("user.dir") + "/resources/Silverware.png"), random.nextInt(10) + 1,  100, 180));
        items.add(new Item("Banana", new ImageIcon(System.getProperty("user.dir") + "/resources/Silverware.png"), random.nextInt(10) + 1,  300, 180));
        items.add(new Item("Cherry", new ImageIcon(System.getProperty("user.dir") + "/resources/Silverware.png"), random.nextInt(10) + 1,  500, 180));
        items.add(new Item("Date", new ImageIcon(System.getProperty("user.dir") + "/resources/Silverware.png"), random.nextInt(10) + 1,  700, 180));

        for (int i = 0; i < items.size(); i++) {
        System.out.println("Name" + items.get(i).getName() + "Price " + items.get(i).getPrice());
        }
    }
    public void drawBarteringTask (Graphics g, int taskTimer, int TASK_TIME_LIMIT){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 250, 100);

        g.setColor(Color.BLACK);
        g.drawString("TASK: Make 1 successful trade with the traders!", 10, 20);
        g.drawString("Score: " + barterScore, 10, 40);
        g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
        g.setColor(Color.RED);


        // Draw items
        for (Item item : items) {
            g.drawImage(item.getImage().getImage(), item.getLocation().x-100, item.getLocation().y, width, height, null);
            g.drawString(item.getName(), item.getLocation().x, item.getLocation().y + height + 20);
        }
        g.drawImage(sortAscButton.getImage().getImage(), sortAscButton.getLocation().x, sortAscButton.getLocation().y, 50, 20, null);
        g.drawImage(sortDesButton.getImage().getImage(), sortDesButton.getLocation().x, sortDesButton.getLocation().y, 50, 20, null);


    }

    private boolean handleItemSelection(Item item) {
        String input = JOptionPane.showInputDialog("Enter the number of beaver furs to barter for the " + item.getName()+ ":");
        if (input != null) {
            try {
                int price = Integer.parseInt(input);
                Item rez = binarySearchName(item.getName());

                System.out.println(rez);
                if ((rez != null) && rez.getPrice() == price) {
                    JOptionPane.showMessageDialog(null, "Successful trade! The trader was willing to sell the " + item.getName() + " for " + item.getPrice() + " furs.");
                    barterScore++;
                    items.remove(item);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Are you trying to scam me? Go away! The trader was NOT willing to sell the " + item.getName() + " for "+ item.getPrice()  + " furs.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
        return false;
    }

    public String barteringMouseHandler (int x, int y, int PLAYER_SIZE) {
        if (sortAscButton.getLocation().x <= x && x <= sortAscButton.getLocation().x + 50 && sortAscButton.getLocation().y <= y && y <= sortAscButton.getLocation().y + 20) {
            shellSortItemsByName(true);
            return "Redraw";
        }

        if (sortDesButton.getLocation().x <= x && x <= sortDesButton.getLocation().x + 50 && sortDesButton.getLocation().y <= y && y <= sortDesButton.getLocation().y + 20) {
            shellSortItemsByName(false);
            return "Redraw";
        }

        for (Item item : items) { //for each item in the bartering task
            Point location = item.getLocation();
            if (x >= location.x-100 && x <= location.x-100 + width +20 && y >= location.y && y <= location.y + height +20) { //if the player is at the item
                if (handleItemSelection(item)) { //if the gather score is 5
                    inBarteringTask = false; //sets that the task has been completed
                    return "C4";
                }
                break;
            }
        }
        return "";
    }

    public void startBarteringTask() {
        inBarteringTask = true;
        barterScore = 0;

        loadItems();

    }

    private void shellSortItemsByName(boolean ascending) {
        int n = items.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
               Item temp = items.get(i);
                int j;
                for (j = i; j >= gap && (ascending ?
                        items.get(j - gap).getName().compareToIgnoreCase(temp.getName()) > 0 :
                        items.get(j - gap).getName().compareToIgnoreCase(temp.getName()) < 0); j -= gap) {

                    // Swap items and their locations
                    Item tempItem = items.get(j);
                    items.set(j, items.get(j - gap));
                    items.set(j - gap, tempItem);

                    // Swap their locations
                    Point tempLocation = tempItem.getLocation();
                    tempItem.setLocation(items.get(j).getLocation());
                    items.get(j).setLocation(tempLocation);
                }
                items.set(j, temp);

                // Update location of the item being inserted
                Point tempLocation = temp.getLocation();
                temp.setLocation(items.get(j).getLocation());
                items.get(j).setLocation(tempLocation);

            }
        }
    }

    private Item binarySearchName(String name) {
        shellSortItemsByName(true);  // Ensure the list is sorted before binary search
        int low = 0;
        int high = items.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Item midItem = items.get(mid);
            int comparison = midItem.getName().compareToIgnoreCase(name);

            if (comparison == 0) {
                return midItem;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    private void shellSortItemsByPrice(boolean ascending) {
        int n = items.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                Item temp = items.get(i);
                int j;
                for (j = i; j >= gap && (ascending ? items.get(j - gap).getPrice() > temp.getPrice() : items.get(j - gap).getPrice() < temp.getPrice()); j -= gap){

                    // Swap items and their locations
                    Item tempItem = items.get(j);
                    items.set(j, items.get(j - gap));
                    items.set(j - gap, tempItem);

                    // Swap their locations
                    Point tempLocation = tempItem.getLocation();
                    tempItem.setLocation(items.get(j).getLocation());
                    items.get(j).setLocation(tempLocation);
                }
                items.set(j, temp);

                // Update location of the item being inserted
                Point tempLocation = temp.getLocation();
                temp.setLocation(items.get(j).getLocation());
                items.get(j).setLocation(tempLocation);

            }
        }
    }

    private Item binarySearchPrice(int price) {
        shellSortItemsByPrice(true);  // Ensure the list is sorted before binary search
        int low = 0;
        int high = items.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Item midItem = items.get(mid);
            int comparison = Integer.compare(midItem.getPrice(), price);

            if (comparison == 0) {
                return midItem;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }
}
    class Item {
        private String name;
        private ImageIcon image;
        private int Price;

        private int xPos;
        private int yPos;

        public Item(String name, ImageIcon image, int Price, int xPos, int yPos) {
            this.name = name;
            this.image = image;
            this.Price = Price;
            this.xPos = xPos;
            this.yPos = yPos;

        }


        public String getName() {

            return name;
        }

        public ImageIcon getImage() {

            return image;
        }

        public int getPrice() {

            return Price;
        }

        public Point getLocation() {
            return new Point(xPos, yPos);
        }

        public void setLocation (Point location) {
            this.xPos = location.x;
            this.yPos = location.y;
        }
    }




