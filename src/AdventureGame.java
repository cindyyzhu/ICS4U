import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;

public class AdventureGame extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PLAYER_SIZE = 20;
    private static final int TASK_TIME_LIMIT = 10;
    private Image playerImage;

    private int playerX = 50;
    private int playerY = 50;
    private int musicScore = 0;
    private int gatherScore = 0;
    private int huntScore = 0;
    private int barterScore = 0;
    private int noteIndex = 0;
    private int taskTimer = 0;
    private int failureCount = 0;
    private boolean inTask = false;
    private boolean inMusicTask = false;
    private boolean inGatheringTask = false;
    private boolean inHuntingTask = false;
    private boolean inBarteringTask = false;
    private boolean isDialogue = false;
    private boolean showMainScene = true;
    private boolean showTaskScene = false;
    private boolean endScene = false;
    private static final int MAX_FAILURES = 3;
    private String currentScenario = "START";
    private String currentCheckpoint = "C1";
    private Timer taskTimerTimer;
    private Timer animationTimer;
    private Timer animalMovementTimer;
    private List<Point> materials = new ArrayList<>();
    private List<Point> animals = new ArrayList<>();
    private List<ChoicePoint> choicePoints = new ArrayList<>();
    private List<String> completedCheckpoints = new ArrayList<>();

    private final String[] notes = {"a", "b", "c", "d", "e", "f", "g"};
    private final char[] noteKeys = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};

    public AdventureGame() {
        setTitle("Mato’s Fur Trade Journey");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //player image
        try{
            playerImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Mato.png").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        addKeyListener(new KeyHandler());
        addMouseListener(new MouseHandler());
        setFocusable(true);

        choicePoints.add(new ChoicePoint(200, 100, "C1", "Elder"));
        choicePoints.add(new ChoicePoint(300, 200, "C2", "Uncle"));
        choicePoints.add(new ChoicePoint(400, 300, "C3", "Father"));
        choicePoints.add(new ChoicePoint(500, 400, "C4", "Trade Post"));

        // Main game timer to handle task timeouts
        taskTimerTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inTask) {
                    taskTimer++;
                    if (taskTimer > TASK_TIME_LIMIT) {
                        inTask = false;
                        inMusicTask = false;
                        inGatheringTask = false;
                        inHuntingTask = false;
                        inBarteringTask = false;
                        failureCount++;
                        JOptionPane.showMessageDialog(null, "Time's up! You failed the task. Try again.");
                        showMainScene = true;
                        showTaskScene = false;
                        checkEndGame();
                    }
                }
            }
        });
        taskTimerTimer.start();

        // Animation timer to refresh the game screen
        animationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.repaint();
            }
        });
        animationTimer.start();

        showStartScreen();
    }

    private void showStartScreen() {
        JOptionPane.showMessageDialog(null, "Welcome to Mato’s Fur Trade Journey\nClick 'OK' to get started!");
        showInstructionsScreen();
    }

    private void showInstructionsScreen() {
        JOptionPane.showMessageDialog(null, "Mato, who is a young, up and coming fur trader, following in the footsteps of his father in Anishinaabeg tribes, like many boys of his age, must first pass a number of steps before he becomes officially recognized.\nHelp Mato pass each checkpoint and become an official fur trader!\n\nUse arrow keys to move Mato to each checkpoint.");
    }

    private void showDialogue(String checkpoint) {
        isDialogue = true;
        String message = "";
        switch (checkpoint) {
            case "C1":
                message = "Elder: Hello Mato, so I hear you’ve been wanting to become a fur trader like your father… But before you can do that, you must first learn the history behind the fur trade in our community. To learn more about this, why don’t you play some traditional Anishinaabeg music on our flutes?";
                break;
            case "C2":
                message = "Uncle: Hello Mato, so you’ve wanted to learn how to make a bow? Well, oh ho ho, before you can do that, you must collect the materials to make a bow!";
                break;
            case "C3":
                message = "Father: Hello Mato, ready to hunt? Let’s go!";
                break;
            case "C4":
                message = "Bartering Staff: Oh look, there’s a new face! But, first let’s make your first trade!";
                break;
        }
        JOptionPane.showMessageDialog(null, message);
        startTask(checkpoint);
    }

    private void startTask(String checkpoint) {
        isDialogue = false;
        showMainScene = false;
        showTaskScene = true;
        switch (checkpoint) {
            case "C1":
                startMusicTask();
                break;
            case "C2":
                startGatheringTask();
                break;
            case "C3":
                startHuntingTask();
                break;
            case "C4":
                startBarteringTask();
                break;
        }
    }

    private void startMusicTask() {
        inTask = true;
        inMusicTask = true;
        musicScore = 0;
        noteIndex = 0;
        taskTimer = 0;
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
            e.printStackTrace();
        }
    }

    private void handleMusicTask(char keyPressed) {
        char expectedNote = noteKeys[noteIndex];
        if (keyPressed == expectedNote) {
            play(keyPressed); // Play the sound for the correct note
            musicScore++;
            noteIndex++;
            if (noteIndex < notes.length) {
                // Continue to the next note
            } else {
                inMusicTask = false;
                completeTask("C1");
            }
        } else {
            JOptionPane.showMessageDialog(null, "You played the wrong note! Try again.");
            musicScore--;
            noteIndex = 0;

        }
        repaint();
    }


    private void startGatheringTask() {
        inTask = true;
        inGatheringTask = true;
        gatherScore = 0;
        taskTimer = 0;
        materials.clear();

        // Automatically place materials at random positions
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            materials.add(new Point(random.nextInt(WINDOW_WIDTH - PLAYER_SIZE), random.nextInt(WINDOW_HEIGHT - PLAYER_SIZE)));
        }

        JOptionPane.showMessageDialog(null, "Task: Collect 1 of each of the following - wood, braided sinew, sealskin, copper rivets, and animal bone. Click on the materials to collect them.");
    }

    private void startHuntingTask() {
        inTask = true;
        inHuntingTask = true;
        huntScore = 0;
        taskTimer = 0;
        animals.clear();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            animals.add(new Point(random.nextInt(WINDOW_WIDTH - 20), random.nextInt(WINDOW_HEIGHT - 20)));
        }
        JOptionPane.showMessageDialog(null, "Task: Hunt 5 beavers. Click on the moving beavers to hunt them.");

        animalMovementTimer = new Timer (200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveAnimals();
            }
        } );
        animalMovementTimer.start();
    }

    private void moveAnimals() {
        Random random = new Random();
        for (Point animal : animals) {
            animal.x = Math.max(0, Math.min(animal.x + random.nextInt(41) - 10, WINDOW_WIDTH - PLAYER_SIZE));
            animal.y = Math.max(0, Math.min(animal.y + random.nextInt(41) - 10, WINDOW_HEIGHT - PLAYER_SIZE));
        }
        repaint();
    }

    private void startBarteringTask() {
        inTask = true;
        inBarteringTask = true;
        barterScore = 0;
        taskTimer = 0;
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
                completeTask("C4");
                successfulTrade = true;
            } else {
                failureCount++;
                if (price < minPrice) {
                JOptionPane.showMessageDialog(null, "Trader: \"It's obviously not good quality if it's that cheap.\"");
            } else {
                JOptionPane.showMessageDialog(null, "Trader: \"So expensive! How arrogant!\"");
            }}
        }

    }

    private void completeTask(String checkpoint) {
        inTask = false;
        inMusicTask = false;
        inGatheringTask = false;
        inHuntingTask = false;
        inBarteringTask = false;
        showMainScene = true;
        showTaskScene = false;
        if(animalMovementTimer != null) {
            animalMovementTimer.stop();
        }
        completedCheckpoints.add(checkpoint);
        currentCheckpoint = getNextCheckpoint();
        JOptionPane.showMessageDialog(null, "Congratulations! You completed the task successfully.");
        checkEndGame();
    }

    private String getNextCheckpoint() {
        switch (currentCheckpoint) {
            case "C1":
                return "C2";
            case "C2":
                return "C3";
            case "C3":
                return "C4";
            case "C4":
                return "";
            default:
                return "";
        }
    }

    private void checkEndGame() {
        if (completedCheckpoints.contains("C1") && completedCheckpoints.contains("C2") &&
                completedCheckpoints.contains("C3") && completedCheckpoints.contains("C4")) {
            endScene = true;
            if (failureCount > MAX_FAILURES) {
                JOptionPane.showMessageDialog(null, "Elder: Mato, I’m very disappointed in your clumsy skills.");
                int response = JOptionPane.showConfirmDialog(null, "System: Would you like to restart to try again to become a fur trader?", "Restart", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    resetGame();
                } else {
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Elder: Congratulations, Mato! I knew you’d succeed!");
                JOptionPane.showMessageDialog(null, "Congratulations! Mato has become a full-fledged fur trader!");
                System.exit(0);
            }
        }
    }

    private void resetGame() {
        playerX = 50;
        playerY = 50;
        musicScore = 0;
        gatherScore = 0;
        huntScore = 0;
        barterScore = 0;
        noteIndex = 0;
        taskTimer = 0;
        failureCount = 0;
        inTask = false;
        inMusicTask = false;
        inGatheringTask = false;
        inHuntingTask = false;
        inBarteringTask = false;
        isDialogue = false;
        showMainScene = true;
        showTaskScene = false;
        endScene = false;
        currentScenario = "START";
        currentCheckpoint = "C1";
        completedCheckpoints.clear();
        choicePoints.clear();
        choicePoints.add(new ChoicePoint(200, 100, "C1", "Elder"));
        choicePoints.add(new ChoicePoint(300, 200, "C2", "Uncle"));
        choicePoints.add(new ChoicePoint(400, 300, "C3", "Father"));
        choicePoints.add(new ChoicePoint(500, 400, "C4", "Trade Post"));
        showStartScreen();
    }
    private void drawMainScene(Graphics g) {
        for (ChoicePoint choicePoint : choicePoints) {
            if (!completedCheckpoints.contains(choicePoint.checkpoint)) {
                choicePoint.draw(g);
            }
        }

        if(playerImage != null) {
            g.drawImage(playerImage, playerX, playerY, PLAYER_SIZE + 30, PLAYER_SIZE+40, this);
        } else {

        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
    }}

    private void drawTaskScene(Graphics g) {
        if (inMusicTask) {
            g.drawString("Play the notes in order: a, b, c, d, e, f, g", 10, 20);
            for (int i = 0; i < notes.length; i++) {
                if (i == noteIndex) {
                    g.setColor(Color.RED); // Highlight the current note
                } else {
                    g.setColor(Color.BLACK);
                }
                g.drawString(notes[i], 10 + i * 15, 40);
                g.drawRect(10 + i * 15, 30, 10, 20);
            }
        } else if (inGatheringTask) {
            g.drawString("Gather materials", 10, 20);
            g.drawString("Score: " + gatherScore, 10, 40);
            g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
            g.setColor(Color.ORANGE);
            for (Point material : materials) {
                g.fillRect(material.x, material.y, PLAYER_SIZE, PLAYER_SIZE);
            }
        } else if (inHuntingTask) {
            g.drawString("Hunt beavers", 10, 20);
            g.drawString("Score: " + huntScore, 10, 40);
            g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
            g.setColor(Color.RED);
            for (Point animal : animals) {
                g.fillRect(animal.x, animal.y, PLAYER_SIZE, PLAYER_SIZE);
            }
        } else if (inBarteringTask) {
            g.drawString("Barter with the trading staff", 10, 20);
            g.drawString("Score: " + barterScore, 10, 40);
            g.drawString("Time remaining: " + (TASK_TIME_LIMIT - taskTimer) + " seconds", 10, 60);
        }
        g.drawString("Failures: " + failureCount, 10, 80);

    }

    private class GamePanel extends JPanel {
        private Image backgroundImage;

        public GamePanel() {
            try {
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
            }
            if (showMainScene) {
                drawMainScene(g);
            } else if (showTaskScene) {
                drawTaskScene(g);
            }
        }
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            if(inMusicTask && new String(noteKeys).indexOf(e.getKeyChar()) != -1) {
                handleMusicTask(e.getKeyChar());
            }

            if (isDialogue || inTask) {
                return;
            }

            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    playerX = Math.max(playerX - 10, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    playerX = Math.min(playerX + 10, WINDOW_WIDTH - PLAYER_SIZE);
                    break;
                case KeyEvent.VK_UP:
                    playerY = Math.max(playerY - 10, 0);
                    break;
                case KeyEvent.VK_DOWN:
                    playerY = Math.min(playerY + 10, WINDOW_HEIGHT - PLAYER_SIZE);
                    break;
                default:
                    if (inMusicTask && new String(noteKeys).indexOf(e.getKeyChar()) != -1) {
                        handleMusicTask(e.getKeyChar());
                    }
                    break;
            }

            for (ChoicePoint choicePoint : choicePoints) {
                if (!completedCheckpoints.contains(choicePoint.checkpoint) && choicePoint.contains(playerX, playerY)) {
                    currentCheckpoint = choicePoint.checkpoint;
                    showDialogue(currentCheckpoint);
                    break;
                }
            }

            repaint();
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {  //this checks if the player is at a checkpoint
            int x = event.getX() - getInsets().left; //this will get the x and y coordinates of the mouse click and subtract the insets (the border) from the x and y coordinates
            int y = event.getY() - getInsets().top;

            //for checkpoints to determine whether the player is at a checkpoint
            for (ChoicePoint choicePoint : choicePoints) {
                if (!completedCheckpoints.contains(choicePoint.checkpoint) && choicePoint.contains(x, y) && choicePoint.isAvailable()) { //if the player is at a checkpoint and the checkpoint is available (not yet completed)
                    currentCheckpoint = choicePoint.checkpoint;  //set the current checkpoint to the checkpoint the player is at
                    showDialogue(currentCheckpoint); //show the dialogue for the checkpoint
                    return;
                }
            }

            if (inGatheringTask) { //if the player is in the gathering task
                for (Point material : materials) { //for each material
                    if (x >= material.x && x <= material.x + PLAYER_SIZE && y >= material.y && y <= material.y + PLAYER_SIZE) { //if the player is at the material
                        gatherScore++; //increase the gather score
                        materials.remove(material); //remove the material
                        if (gatherScore == 5) { //if the gather score is 5
                            inGatheringTask = false; //sets that the task has been completed
                            completeTask("C2");
                        }
                        break;
                    }
                }
            } else if (inHuntingTask) { //if the player is in the hunting task
                for (Point animal : animals) { //for each animal, point represents the location
                    if (x >= animal.x && x <= animal.x + PLAYER_SIZE && y >= animal.y && y <= animal.y + PLAYER_SIZE) { //if the player is at the animal
                        huntScore++; //increase the hunt score
                        animals.remove(animal); //remove the animal
                        if (huntScore == 5) { //if the hunt score is 5
                            inHuntingTask = false; //sets that the task has been completed
                            completeTask("C3");
                        }
                        break;
                    }
                }
            }
            repaint(); //reset the screen
        }
    }

    private class ChoicePoint {
        int x, y;
        String checkpoint;
        String description;
        private Image image;

        public ChoicePoint(int x, int y, String checkpoint, String description) {
            this.x = x;
            this.y = y;
            this.checkpoint = checkpoint;
            this.description = description;
            loadImage();
        }

        private void loadImage () {
            try{
                switch(checkpoint) {
                    case "C1":
                        image = new ImageIcon(System.getProperty("user.dir") + "/resources/Elder.png").getImage();
                        break;

                    case "C2":
                        image = new ImageIcon(System.getProperty("user.dir") + "/resources/Basket.png").getImage();
                        break;

                    case "C3":
                        image = new ImageIcon(System.getProperty("user.dir") + "/resources/Bow.png").getImage();
                        break;

                    case "C4":
                        image = new ImageIcon(System.getProperty("user.dir") + "/resources/Trade.png").getImage();
                        break;

                    default:
                        image = null;
                        break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void draw(Graphics g) {
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int textWidth = metrics.stringWidth(description);
            int textHeight = metrics.getHeight();

            int imageWidth = 100;
            int imageHeight = 100;

            int textX = x + (imageWidth - textWidth) / 2;
            int textY = y + imageHeight + textHeight;

            g.setColor(Color.WHITE);
            g.fillRect(textX-10, textY-textHeight+2, textWidth + 20, textHeight+1);

            g.setColor(Color.BLACK);
            g.drawString(description, textX, textY);

            if (image != null) {
                switch (checkpoint)
                {
                    case "C1":
                        g.drawImage(image, x, y, imageWidth, imageHeight, null);
                        break;

                    case "C2":
                        g.drawImage(image, x, y, imageWidth, imageHeight, null);
                        break;

                    case "C3":
                        g.drawImage(image, x, y, imageWidth, imageHeight, null);
                        break;

                    case "C4":
                        g.drawImage(image, x, y, imageWidth, imageHeight, null);
                        break;
            } }else {
            }}

        public boolean contains(int px, int py) {
            // Check if the click is within the image bounds
            if (px >= x && px <= x + 50 && py >= y && py <= y + 50) {
                return true;
            }
            // Check if the click is within the text bounds
            FontMetrics metrics = getGraphics().getFontMetrics();
            int textWidth = metrics.stringWidth(description);
            if (px >= x - 10 && px <= x - 10 + textWidth + 20 && py >= y - 30 && py <= y - 30 + metrics.getHeight()) {
                return true;
            }
            return false;
        }


        public boolean isAvailable() {
            // Check if the previous checkpoint is completed
            switch (checkpoint) {
                case "C2":
                    return completedCheckpoints.contains("C1");
                case "C3":
                    return completedCheckpoints.contains("C2");
                case "C4":
                    return completedCheckpoints.contains("C3");
                default:
                    return true;
            }
    }}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdventureGame game = new AdventureGame();
            game.setVisible(true);
        });
    }
}
