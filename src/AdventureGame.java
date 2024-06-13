import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

//importing the other classes and packages
import tasks.MusicTask;
import tasks.GatheringTask;
import tasks.HuntingTask;
import tasks.BarteringTask;

public class AdventureGame extends JFrame {
    //initializes the tasks and constructors for the Adventure Game
    MusicTask musicTask = new MusicTask();
    GatheringTask gatheringTask = new GatheringTask();
    HuntingTask huntingTask = new HuntingTask();
    BarteringTask barteringTask = new BarteringTask();
    public boolean inTask = false;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PLAYER_SIZE = 20;
    private Image playerImage;
    private Image endSceneImage;
    private int playerX = 50;
    private int playerY = 50;
    private int failureCount = 0;
    private boolean isDialogue = false;
    private boolean showMainScene = true;
    private boolean showTaskScene = false;
    private boolean endScene = false;
    private static final int MAX_FAILURES = 3;
    private String currentCheckpoint = "C1";
    private List<ChoicePoint> choicePoints = new ArrayList<>();
    private List<String> completedCheckpoints = new ArrayList<>();
    public Timer taskTimerTimer;
    public static final int TASK_TIME_LIMIT = 1000000;

    public Timer animationTimer;
    public int taskTimer = 0;

    private IntroductionPanel introductionPanel;
    private InstructionPanel instructionPanel;
    private GamePanel gamePanel;
    private EndScenePanel endScenePanel;

    private JLabel msgLabel;

    public AdventureGame() {
        //opening screen
        setTitle("Mato’s Fur Trade Journey");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        //load player image in opening screen
        try {
            playerImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Mato.png").getImage();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //initialize
        msgLabel = new JLabel();

        addKeyListener(new KeyHandler());
        addMouseListener(new MouseHandler());
        setFocusable(true); // Set the JFrame to focusable so it can receive key events

        introductionPanel = new IntroductionPanel(this);
        add(introductionPanel);

        instructionPanel = new InstructionPanel(this);
        instructionPanel.setVisible(false);
        instructionPanel.setEnabled(false);

        gamePanel = new GamePanel(this);
        gamePanel.setVisible(false);
        gamePanel.setEnabled(false);

        endScenePanel = new EndScenePanel(this);
        endScenePanel.setVisible(false);
        endScenePanel.setEnabled(false);

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
                        musicTask.inMusicTask = false;
                        gatheringTask.inGatheringTask = false;
                        huntingTask.inHuntingTask = false;
                        barteringTask.inBarteringTask = false;
                        failureCount++;
                        JOptionPane.showMessageDialog(null, "Time's up! You failed the task. Try again.");
                        showMainScene = true;
                        showTaskScene = false;
                        checkEndGame(AdventureGame.this);
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

        //showStartScreen();
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
        // JOptionPane.showMessageDialog(null, message);
        msgLabel.setText(message);
        msgLabel.setVisible(true);
        startTask(checkpoint);

    }

    private void startTask(String checkpoint) {
        isDialogue = false;
        showMainScene = false;
        showTaskScene = true;
        taskTimer = 0;
        inTask = true;
        switch (checkpoint) {
            case "C1":
                musicTask.startMusicTask();
                break;
            case "C2":
                gatheringTask.startGatheringTask();
                break;
            case "C3":
                huntingTask.startHuntingTask(WINDOW_WIDTH, WINDOW_HEIGHT);
                break;
            case "C4":
                barteringTask.startBarteringTask();

                break;
        }
        repaint();
    }

    private void completeTask(String checkpoint) {
        msgLabel.setVisible(false);
        inTask = false;
        musicTask.inMusicTask = false;
        gatheringTask.inGatheringTask = false;
        huntingTask.inHuntingTask = false;
        barteringTask.inBarteringTask = false;
        showMainScene = true;
        showTaskScene = false;
        if (huntingTask.animalMovementTimer != null) {
            huntingTask.animalMovementTimer.stop();
        }
        completedCheckpoints.add(checkpoint);
        JOptionPane.showMessageDialog(null, "Congratulations! You completed the task successfully.");
        checkEndGame(AdventureGame.this);
    }

    private void resetGame(AdventureGame game) {
        playerX = 50;
        playerY = 50;
        failureCount = 0;
        musicTask.resetTasks();//add the other methods
        barteringTask.resetTasks();
        gatheringTask.resetTasks();
        huntingTask.resetTasks();
        isDialogue = false;
        showMainScene = true;
        showTaskScene = false;
        endScene = false;
        currentCheckpoint = "C1";
        completedCheckpoints.clear();
        taskTimer = 0;
        inTask = false;
        choicePoints.clear();
        choicePoints.add(new ChoicePoint(200, 100, "C1", "Elder"));
        choicePoints.add(new ChoicePoint(300, 200, "C2", "Uncle"));
        choicePoints.add(new ChoicePoint(400, 300, "C3", "Father"));
        choicePoints.add(new ChoicePoint(500, 400, "C4", "Trade Post"));
        game.add(introductionPanel);
        introductionPanel.setVisible(true);
        introductionPanel.setEnabled(true);
    }

    private void drawMainScene(Graphics g) {
        for (ChoicePoint choicePoint : choicePoints) {
            if (!completedCheckpoints.contains(choicePoint.checkpoint)) {
                choicePoint.draw(g);
            }
        }

        if (playerImage != null) {
            g.drawImage(playerImage, playerX, playerY, PLAYER_SIZE + 30, PLAYER_SIZE + 40, this);
        } else {

            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        }
    }

    private void drawTaskScene(Graphics g) {
        if (musicTask.inMusicTask) {
            musicTask.drawMusicTask(g);
        } else if (gatheringTask.inGatheringTask) {
            gatheringTask.drawGatheringTask(g, taskTimer, PLAYER_SIZE, TASK_TIME_LIMIT);
        } else if (huntingTask.inHuntingTask) {
            huntingTask.drawHuntingTask(g, taskTimer, PLAYER_SIZE, TASK_TIME_LIMIT);
        } else if (barteringTask.inBarteringTask) {
            barteringTask.drawBarteringTask(g, taskTimer, TASK_TIME_LIMIT);
        }
        g.drawString("Failures: " + failureCount, 10, 80);
    }

    private class IntroductionPanel extends JPanel {
        private Image backgroundImage;

        public IntroductionPanel(AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            JLabel introLabel = new JLabel("<html>Welcome to Mato’s<br>Fur Trade Journey!</br></html>", SwingConstants.CENTER);
            introLabel.setSize(400, 100);
            introLabel.setLocation(200, 10);
            introLabel.setFont(new Font("Arial", Font.BOLD, 30));
            introLabel.setForeground(Color.BLACK);
            add(introLabel);

            //button to go to the next screen
            JButton startButton = new JButton("Play!");
            startButton.setSize(100, 50);
            startButton.setLocation(350, 450);
            startButton.setFont(new Font("Arial", Font.BOLD, 20));
            startButton.setForeground(Color.BLACK);
            startButton.setBackground(Color.WHITE);
            add(startButton);


            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    introductionPanel.setVisible(false);
                    introductionPanel.setEnabled(false);
                    game.add(instructionPanel);
                    instructionPanel.setVisible(true);
                    instructionPanel.setEnabled(true);

                }
            });

            try {
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();

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

        }
    }

    private class InstructionPanel extends JPanel {
        private Image backgroundImage;
        private Image instructionText;

        public InstructionPanel(AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            //button to go to the next screen
            JButton startButton = new JButton("Got It!");
            startButton.setSize(100, 50);
            startButton.setLocation(350, 450);
            startButton.setFont(new Font("Arial", Font.BOLD, 20));
            startButton.setForeground(Color.BLACK);
            startButton.setBackground(Color.WHITE);
            add(startButton);


            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    instructionPanel.setVisible(false);
                    instructionPanel.setEnabled(false);
                    game.add(gamePanel);
                    gamePanel.setVisible(true);
                    gamePanel.setEnabled(true);

                }
            });

            try {
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();
                instructionText = new ImageIcon(System.getProperty("user.dir") + "/resources/Instruction-text.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
                g.drawImage(instructionText, WINDOW_WIDTH / 2 - 300, WINDOW_HEIGHT / 2 - 225, 600, 350, null);
            }

        }
    }

    private class GamePanel extends JPanel {
        private Image backgroundImage;

        public GamePanel(AdventureGame game) {
            add(msgLabel);
            msgLabel.setBounds(500, 500, 800, 50);
            msgLabel.setFont(new Font("Arial", Font.BOLD, 20));
            msgLabel.setVisible(false);

            try {
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }

           /* if (endScene) {
                gamePanel.setVisible(false);
                gamePanel.setEnabled(false);
                game.add(endScenePanel);
                endScenePanel.setVisible(true);
                endScenePanel.setEnabled(true);
            }*/
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
            /*else if (endScene) {
                drawEndScene(AdventureGame.this);
            }*/
        }
    }

    private void checkEndGame(AdventureGame game) {
        if (completedCheckpoints.contains("C1") && completedCheckpoints.contains("C2") &&
                completedCheckpoints.contains("C3") && completedCheckpoints.contains("C4")) {
            endScene = true;
            endScenePanel.setVisible(true);
            endScenePanel.setEnabled(true);
            game.add(endScenePanel);
            //endScenePanel.repaint();
            //endScenePanel.revalidate();
            gamePanel.setVisible(false);
            gamePanel.setEnabled(false);
            taskTimerTimer.stop();
            animationTimer.stop();


            /*try {
                endSceneImage = new ImageIcon(System.getProperty("user.dir") + "/resources/EndScene.jpeg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            showMainScene = false;
            showTaskScene = false;
            if (failureCount > MAX_FAILURES) {
                JOptionPane.showMessageDialog(null, "Elder: Mato, I’m very disappointed in your clumsy skills.\nClearly, you are not suited for the fur trade, as you've already failed " + failureCount + " times.");
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
            }*/
        }
    }

   /* private class failedEndScenePanel extends JPanel {
        private Image backgroundImage;
        public FailedEndScenePanel (AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            //button to go to the next screen
            JButton startButton = new JButton("Play!");
            startButton.setSize(100, 50);
            startButton.setLocation(350, 450);
            startButton.setFont(new Font("Arial", Font.BOLD, 20));
            startButton.setForeground(Color.BLACK);
            startButton.setBackground(Color.WHITE);
            add(startButton);


            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    instructionPanel.setVisible(false);
                    instructionPanel.setEnabled(false);
                    game.add(gamePanel);
                    gamePanel.setVisible(true);
                    gamePanel.setEnabled(true);

                }
            });

            try {
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();
                failedEndSceneText = new ImageIcon(System.getProperty("user.dir") + "/resources/Instruction-text.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
                g.drawImage(failedEndSceneText, WINDOW_WIDTH / 2 - 300, WINDOW_HEIGHT / 2 - 225, 600, 350, null);
            }

        }
        }
    }*/

    /*private void drawEndScene(AdventureGame game) {
        gamePanel.setVisible(false);
        gamePanel.setEnabled(false);
        game.add(endScenePanel);
        endScenePanel.setVisible(true);
        endScenePanel.setEnabled(true);
    }*/

    private class EndScenePanel extends JPanel {
        private Image backgroundImage;
        private Image endScenePanelText;

        public EndScenePanel(AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            //button to exit the game
            JButton exitButton = new JButton("Exit Game");
            exitButton.setSize(200, 50);
            exitButton.setLocation(150, 450);
            exitButton.setFont(new Font("Arial", Font.BOLD, 20));
            exitButton.setForeground(Color.BLACK);
            exitButton.setBackground(Color.WHITE);
            add(exitButton);

            //button to play again
            JButton playAgainButton = new JButton("Play Again!");
            playAgainButton.setSize(200, 50);
            playAgainButton.setLocation(450, 450);
            playAgainButton.setFont(new Font("Arial", Font.BOLD, 20));
            playAgainButton.setForeground(Color.BLACK);
            playAgainButton.setBackground(Color.WHITE);
            add(playAgainButton);

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //leave the game
                    System.exit(0);
                }
            });

            playAgainButton.addActionListener (new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //play the game again
                    resetGame(AdventureGame.this);
                    game.add(introductionPanel);
                    endScenePanel.setVisible(false);
                    endScenePanel.setEnabled(false);
                }
            });

            try {
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();
                endScenePanelText = new ImageIcon(System.getProperty("user.dir") + "/resources/endScenePanelText.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
                g.drawImage(endScenePanelText, WINDOW_WIDTH / 2 - 300, WINDOW_HEIGHT / 2 - 225, 600, 350, null);
            }

        }

        }



    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            //msgLabel.setVisible(false);

            String returnMusicTask = musicTask.handleMusicTask(e.getKeyChar());
            if (!returnMusicTask.isEmpty()) {
                completeTask(returnMusicTask);
            }
            repaint();

            if (inTask || isDialogue) {
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
            }

            for (ChoicePoint choicePoint : choicePoints) {
                if (!completedCheckpoints.contains(choicePoint.checkpoint) && choicePoint.contains(playerX, playerY)) {
                    currentCheckpoint = choicePoint.checkpoint;
                    if (!inTask) {
                    showDialogue(currentCheckpoint); }
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

            //msgLabel.setVisible(false);

            if (!inTask){
            //for checkpoints to determine whether the player is at a checkpoint
            for (ChoicePoint choicePoint : choicePoints) {
                if (!completedCheckpoints.contains(choicePoint.checkpoint) && choicePoint.contains(x, y)) { //if the player is at a checkpoint and the checkpoint is available (not yet completed)
                    currentCheckpoint = choicePoint.checkpoint;  //set the current checkpoint to the checkpoint the player is at
                    showDialogue(currentCheckpoint); //show the dialogue for the checkpoint

                    return;
                }
            }}

            if (gatheringTask.inGatheringTask) { //if the player is in the gathering task
                String returnGatheringTask = gatheringTask.gatheringMouseHandler(x, y, PLAYER_SIZE); //call the gathering mouse handler
                if (!returnGatheringTask.isEmpty()) {
                    completeTask(returnGatheringTask);
                }

            }
            if (huntingTask.inHuntingTask) { //if the player is in the hunting task
                String returnHuntingTask = huntingTask.huntingMouseHandler(x, y, PLAYER_SIZE); //call the hunting mouse handler
                if (!returnHuntingTask.isEmpty()) {
                    completeTask(returnHuntingTask);

                }
            }

            if (barteringTask.inBarteringTask) { //if the player is in the bartering task
                String returnBarteringTask = barteringTask.barteringMouseHandler(x, y, PLAYER_SIZE); //call the bartering mouse handler
                if (returnBarteringTask == "C4") {
                    completeTask(returnBarteringTask);
                } else if (returnBarteringTask == "Redraw") {
                    barteringTask.drawBarteringTask(getGraphics(), taskTimer, TASK_TIME_LIMIT);
                }
                else  {

                    /*failureCount++;
                    inTask = false;
                    barteringTask.inBarteringTask = false;
                    showMainScene = true;*/
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
        }}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdventureGame game = new AdventureGame();
            game.setVisible(true);
        });
    }
    }
