package tasks;

import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.util.*;

public class BarteringTask extends Tasks {
    //intializing constructor values
    private ArrayList<Item> items;
    private int height = 200;
    private int width = 200;
    private Item sortAscButton;
    private Item sortDesButton;

    public BarteringTask() {
        items = new ArrayList<>();
        loadItems();

        // initialize the sort buttons
        sortAscButton = new Item("Sort Ascending", new ImageIcon(System.getProperty("user.dir") + "/resources/AscSort.png"), 0, 525, 35);
        sortDesButton = new Item("Sort Descending", new ImageIcon(System.getProperty("user.dir") + "/resources/DescSort.png"), 0, 650, 35);

    }

    //adds the items displayed into the array list
    private void loadItems() {
        items.clear();
        Random random = new Random();
        items.add(new Item("Silver Tea Set", new ImageIcon(System.getProperty("user.dir") + "/resources/Silverware.png"), random.nextInt(10) + 1, 100, 180));
        items.add(new Item("Iron Knife", new ImageIcon(System.getProperty("user.dir") + "/resources/Knife.png"), random.nextInt(10) + 1, 300, 180));
        items.add(new Item("Imported Mirror", new ImageIcon(System.getProperty("user.dir") + "/resources/Mirror.png"), random.nextInt(10) + 1, 500, 180));
        items.add(new Item("Glass Beads", new ImageIcon(System.getProperty("user.dir") + "/resources/GlassBeads.png"), random.nextInt(10) + 1, 700, 180));

    }

    //draws the bartering task
    public void drawBarteringTask(Graphics g) {
        Image shelf;
        try { //background image
            shelf = new ImageIcon(System.getProperty("user.dir") + "/resources/Shelf.png").getImage();
            g.drawImage(shelf, -10, -50, 810, 650, null);
        } catch (Exception e) {
        }

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 250, 100); //white rectangle to see the black text on the background

        g.setColor(Color.BLACK); //text
        g.drawString("TASK: Make 1 successful trade with the traders!", 10, 20);

        // draw items on display
        for (Item item : items) {
            g.drawImage(item.getImage().getImage(), item.getLocation().x - 100, item.getLocation().y, width, height, null);
            g.drawString(item.getName(), item.getLocation().x - 40, item.getLocation().y + height + 20);
        }

        // draw sort buttons
        g.drawImage(sortAscButton.getImage().getImage(), sortAscButton.getLocation().x, sortAscButton.getLocation().y, 100, 40, null);
        g.drawImage(sortDesButton.getImage().getImage(), sortDesButton.getLocation().x, sortDesButton.getLocation().y, 100, 40, null);

        g.setColor(Color.RED);

    }

    //handles the selection of the item once clicked
    private boolean handleItemSelection(Item item) {
        JFrame f = new JFrame(); //this jframe is required to be able to add an image into the joption display panel
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "/resources/Beaver.jpg");
        icon = new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)); //scaling it
        String input = (String) JOptionPane.showInputDialog(f, "Enter the number of beaver furs to barter for the " + item.getName() + ":", "Input", JOptionPane.QUESTION_MESSAGE, icon, null, null);
        if (input != null) {
            try {
                int price = Integer.parseInt(input); //parse the input into an integer
                Item rez = binarySearchName(item.getName()); //search for the item in the list

                if ((rez != null) && rez.getPrice() == price) { //if the item is found and the price is correct
                    JOptionPane.showMessageDialog(null, "Successful trade! The trader was willing to sell the " + item.getName() + " for " + item.getPrice() + " furs.", "Trade Successful", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(item.getImage().getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                    items.remove(item); //remove the item from the list
                    return true;
                } else if (rez.getPrice() < price) { //if the price is too high
                    JOptionPane.showMessageDialog(null, "This is clearly WORTH LESS than what you're offering! Are you trying to scam me? Go away!", "Trade Failed", JOptionPane.ERROR_MESSAGE, new ImageIcon(item.getImage().getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                } else if (rez.getPrice() > price) { //if the price is too low
                    JOptionPane.showMessageDialog(null, "How arrogant! This is clearly WORTH MORE than what you're offering! Are you trying to scam me? Go away!", "Trade Failed", JOptionPane.ERROR_MESSAGE, new ImageIcon(item.getImage().getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                }
            } catch (NumberFormatException e) { //data validation, makes sure it's valid input
                JOptionPane.showMessageDialog(null, "Invalid input - Please enter a valid number.");
            }
        }
        return false;
    }


    //once clicked
    public String barteringMouseHandler(int x, int y) {
        //if on asc button, it will re-sort
        if (sortAscButton.getLocation().x <= x && x <= sortAscButton.getLocation().x + 100 && sortAscButton.getLocation().y <= y && y <= sortAscButton.getLocation().y + 40) {
            shellSortItemsByName(false, items);
            return "Redraw";
        }

        //if on des button, it will re-sort
        if (sortDesButton.getLocation().x <= x && x <= sortDesButton.getLocation().x + 100 && sortDesButton.getLocation().y <= y && y <= sortDesButton.getLocation().y + 40) {
            shellSortItemsByName(true, items);
            return "Redraw";
        }

        for (Item item : items) { //for each item in the bartering task
            Point location = item.getLocation();
            if (x >= location.x - 100 && x <= location.x - 100 + width + 20 && y >= location.y && y <= location.y + height + 20) { //if the player is at the item
                if (handleItemSelection(item)) {
                    inBarteringTask = false; //sets that the task has been completed
                    return "C4";
                }
                break;
            }
        }
        return ""; //incomplete task
    }

    //allows the task to start
    public void startBarteringTask() {

        inBarteringTask = true;
    }

    //sorts the items by name
    private ArrayList<Item> shellSortItemsByName(boolean descending, ArrayList<Item> tempItems) {
        int n = tempItems.size(); //size of the list
        for (int gap = n / 2; gap > 0; gap /= 2) { //for each gap
            for (int i = gap; i < n; i++) { //for each item
                Item temp = tempItems.get(i); //get the item
                int j; //initialize j
                for (j = i; j >= gap && (descending ? tempItems.get(j - gap).getName().compareToIgnoreCase(temp.getName()) > 0 : tempItems.get(j - gap).getName().compareToIgnoreCase(temp.getName()) < 0); j -= gap) {

                    Item tempItem = tempItems.get(j); //get the item

                    // swap their locations
                    Point tempLocation = tempItem.getLocation();
                    tempItem.setLocation(tempItems.get(j).getLocation());
                    tempItems.get(j).setLocation(tempLocation);

                    // swap the items
                    tempItems.set(j, tempItems.get(j - gap));
                    tempItems.set(j - gap, tempItem);


                }
                // ppdate location of the item being inserted
                Point tempLocation = temp.getLocation();
                temp.setLocation(tempItems.get(j).getLocation());
                tempItems.get(j).setLocation(tempLocation);

                // insert the item
                tempItems.set(j, temp);

            }
        }
        // return the sorted list
        return tempItems;
    }

    //searches for the item
    private Item binarySearchName(String name) {
        ArrayList<Item> newItems = shellSortItemsByName(true, (ArrayList<Item>)items.clone());  // Ensure the list is sorted before binary search using a clone so it doesn't touch the original one

        // binary search
        int low = 0;
        int high = newItems.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2; //get the middle item
            Item midItem = newItems.get(mid);
            int comparison = midItem.getName().compareToIgnoreCase(name); //compare the name

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

        //constructor for the different items
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




