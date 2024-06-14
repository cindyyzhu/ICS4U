//importing the necessary libraries
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
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
    //initializes the tasks and constructors for the AdventureGame class for the game
    
    //identify the different tasks
    MusicTask musicTask = new MusicTask();
    GatheringTask gatheringTask = new GatheringTask();
    HuntingTask huntingTask = new HuntingTask();
    BarteringTask barteringTask = new BarteringTask();
    public boolean inTask = false;
    
    //initialize the window size, player size, and images
    private static final int windowWidth = 800;
    private static final int windowHeight = 600;
    private static final int playerSize = 20;
    
    //initializing the images
    private Image playerImage;

    //initializing the different points
    private int playerX = 50;
    private int playerY = 50;
    private int failureCount = 0;
    private boolean isDialogue = false;
    private boolean showMainScene = true;
    private boolean showTaskScene = false;
    private static final int maxFails = 3;
    
    //current choicepoints
    private String currentCheckpoint = "C1";
    private final List<ChoicePoint> choicePoints = new ArrayList<>();
    private final List<String> completedCheckpoints = new ArrayList<>();
    
    //time limit for the hunting and gathering tasks
    public static final int taskTimeLimit = 10;
    public Timer taskTimerTimer;
    public Timer animationTimer;
    public int taskTimer = 0;

    //for the different panels (screens/scenes in the game)
    private final IntroductionPanel introductionPanel;
    private final InstructionPanel instructionPanel;
    private final GamePanel gamePanel;
    private final EndScenePanel endScenePanel;
    private final FailedEndScenePanel failedEndScenePanel;

    private final JLabel msgLabel;

    public AdventureGame() {
        //opening screen
        setTitle("Mato’s Fur Trade Adventure!");
        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //load player image in opening screen
        try {
            playerImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Mato.png").getImage();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //initializing the labels and the panels
        msgLabel = new JLabel();
        msgLabel.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), null));

        addKeyListener(new KeyHandler());
        addMouseListener(new MouseHandler());
        setFocusable(true); //allow it to receive key events
        
        //initialize the different panels
        //intro panel (1st panel)
        introductionPanel = new IntroductionPanel(this);
        add(introductionPanel);

        //instruction panel (2nd panel)
        instructionPanel = new InstructionPanel(this);
        instructionPanel.setVisible(false);
        instructionPanel.setEnabled(false);

        //game panel (main panel/3rd panel)
        gamePanel = new GamePanel(this);
        gamePanel.setVisible(false);
        gamePanel.setEnabled(false);

        //successful end
        endScenePanel = new EndScenePanel(this);
        endScenePanel.setVisible(false);
        endScenePanel.setEnabled(false);

        //failed end
        failedEndScenePanel = new FailedEndScenePanel(this);
        failedEndScenePanel.setVisible(false);
        failedEndScenePanel.setEnabled(false);
        
        //different choicepoints and positions of each checkpoint
        choicePoints.add(new ChoicePoint(200, 100, "C1", "Elder"));
        choicePoints.add(new ChoicePoint(300, 200, "C2", "Uncle"));
        choicePoints.add(new ChoicePoint(400, 300, "C3", "Father"));
        choicePoints.add(new ChoicePoint(500, 400, "C4", "Trade Post"));

        // timer for the hunting and gathering tasks
        taskTimerTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //if the msg label has been clicked, and it's in the hunting/gathering task, timer to limit that the task must be completed in 10 seconds
                if (inTask && !musicTask.inMusicTask && !barteringTask.inBarteringTask && !msgLabel.isVisible()){
                    taskTimer++;
                    if (taskTimer > taskTimeLimit) { //once the timer is greater than 10 seconds, the task is failed, and it exits the task
                        inTask = false;
                        gatheringTask.inGatheringTask = false;
                        huntingTask.inHuntingTask = false;
                        failureCount++;
                        JOptionPane.showMessageDialog(null, "Time's up! You failed the task :( Try again.");
                        showMainScene = true;
                        showTaskScene = false;
                        taskTimer = 0;
                        checkEndGame(AdventureGame.this);
                        msgLabel.setVisible(false); //hides the message label

                    }
                    repaint(); //resets the screen
                }
            }
        });
        taskTimerTimer.start(); //starts the timer

        // animation timer to determine the beaver movement in the hunting task
        animationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (huntingTask.inHuntingTask) {
                
                gamePanel.repaint(); }
            }
        });
        animationTimer.start();

    }

    //dialogues of the different checkpoints
    private void showDialogue(String checkpoint) {
        isDialogue = true;
        Image dialogueText;
        String checkPointImage = "";

        switch (checkpoint) {
            case "C1":
                checkPointImage = "C1";
                break;
            case "C2":
                checkPointImage = "C2";
                break;
            case "C3":
                checkPointImage = "C3";
                break;
            case "C4":
                checkPointImage = "C4";
                break;
        }
        //dialogue image
        dialogueText = new ImageIcon(System.getProperty("user.dir") + "/resources/" + checkPointImage + ".png").getImage();
        Image scaledImage = dialogueText.getScaledInstance(800, 600, Image.SCALE_DEFAULT);

        //set the dialogue image into a Jlabel and show it
        msgLabel.setIcon(new ImageIcon(scaledImage));
        startTask(checkpoint);
        msgLabel.setVisible(inTask);

    }

    //to start each task
    private void startTask(String checkpoint) {
        switch (checkpoint) {
            case "C1":
                musicTask.startMusicTask();
                break;
            case "C2":
                gatheringTask.startGatheringTask();
                break;
            case "C3":
                huntingTask.startHuntingTask(windowWidth, windowHeight);
                break;
            case "C4":
                barteringTask.startBarteringTask();

                break;
        }
        
        isDialogue = false;
        showMainScene = false;
        showTaskScene = true;
        taskTimer = 0;
        inTask = true;
        repaint();
        
    }
    
    //to mark each task as complete
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

    //resets the game if the user wants to play again
    private void resetGame(AdventureGame game) {
        playerX = 50;
        playerY = 50;
        failureCount = 0;
        musicTask.resetTasks();
        barteringTask.resetTasks();
        gatheringTask.resetTasks();
        huntingTask.resetTasks();
        isDialogue = false;
        showMainScene = true;
        showTaskScene = false;
        currentCheckpoint = "C1";
        completedCheckpoints.clear();
        taskTimer = 0;
        taskTimerTimer.restart();
        animationTimer.restart();
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

    //draws the main scene with all the different checkpoints
    private void drawMainScene(Graphics g) {
        for (ChoicePoint choicePoint : choicePoints) {
            if (!completedCheckpoints.contains(choicePoint.checkpoint)) {
                choicePoint.draw(g);
            }
        }

        //draws the player image
        if (playerImage != null) {
            g.drawImage(playerImage, playerX, playerY, playerSize + 30, playerSize + 40, this);
        } else {

            g.setColor(Color.BLUE); //a blue icon will show up if the player image doesn't work
            g.fillRect(playerX, playerY, playerSize, playerSize);
        }
    }

    //draws the task scene for each
    private void drawTaskScene(Graphics g) {
        if (musicTask.inMusicTask) {
            musicTask.drawMusicTask(g);
        } else if (gatheringTask.inGatheringTask) {
            gatheringTask.drawGatheringTask(g, taskTimer, playerSize, taskTimeLimit);
        } else if (huntingTask.inHuntingTask) {
            huntingTask.drawHuntingTask(g, taskTimer, playerSize, taskTimeLimit);
        } else if (barteringTask.inBarteringTask) {
            barteringTask.drawBarteringTask(g);
        }
        g.drawString("Failures: " + failureCount, 10, 80);

    }

    //introductory panel scene
    private class IntroductionPanel extends JPanel {
        private Image backgroundImage;

        public IntroductionPanel(AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(windowWidth, windowHeight));
            
            //label for the title of the game
            JLabel introLabel = new JLabel("<html>Welcome to Mato’s<br>Fur Trade Adventure!</br></html>", SwingConstants.CENTER);
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


            //for if the button is clicked
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    introductionPanel.setVisible(false); //goes to the instruction panel, and hides this one
                    introductionPanel.setEnabled(false);
                    game.add(instructionPanel);
                    instructionPanel.setVisible(true);
                    instructionPanel.setEnabled(true);

                }
            });

            try { //image for the background
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) { //draws the background
                g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
            }

        }
    }

    //instruction panel to give users the instructions
    private class InstructionPanel extends JPanel {
        private Image backgroundImage;
        private Image instructionText; //the text for the instruction panel

        public InstructionPanel(AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(windowWidth, windowHeight));

            //button to go to the next screen
            JButton startButton = new JButton("Got It!");
            startButton.setSize(100, 50);
            startButton.setLocation(350, 450);
            startButton.setFont(new Font("Arial", Font.BOLD, 20));
            startButton.setForeground(Color.BLACK);
            startButton.setBackground(Color.WHITE);
            add(startButton);
            
            //for if the button is clicked
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    instructionPanel.setVisible(false); //goes to the game panel, and hides this one
                    instructionPanel.setEnabled(false);
                    game.add(gamePanel);
                    gamePanel.setVisible(true);
                    gamePanel.setEnabled(true);
                    
                }
            });

            try { //shows the background and text
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();
                instructionText = new ImageIcon(System.getProperty("user.dir") + "/resources/Instruction-text.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null && instructionText != null) { //draws the background and text
                g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
                g.drawImage(instructionText, windowWidth / 2 - 300, windowHeight / 2 - 225, 600, 350, null);
            }

        }
    }

    //shows the game panel with the different checkpoints and the different tasks
    private class GamePanel extends JPanel {
        private Image backgroundImage;

        public GamePanel(AdventureGame game) {
            add(msgLabel);
            msgLabel.setBounds(0, 0, 800, 600);
            msgLabel.setVisible(false);

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
                g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
            }

            //draws the different scenes depending on what is currently in progress
            if (showMainScene) { 
                drawMainScene(g);
            }
            else if (showTaskScene) {
                drawTaskScene(g);
            }

        }
    }

    //check whether the user has completed all the checkpoints or not
    private void checkEndGame(AdventureGame game) { //if completed all the checkpoints
        if (completedCheckpoints.contains("C1") && completedCheckpoints.contains("C2") && completedCheckpoints.contains("C3") && completedCheckpoints.contains("C4")) {
            taskTimerTimer.stop();
            animationTimer.stop();
            
            if (failureCount > maxFails) { //if they failed more than 3 times, they will see a failed end scene, which will prompt the users to play the game again
                failedEndScenePanel.setVisible(true);
                failedEndScenePanel.setEnabled(true);
                game.add(failedEndScenePanel);
                gamePanel.setVisible(false);
                gamePanel.setEnabled(false);
            } else{
                //shows the successful end scene panel, gives the user the option to play again or exit if wanted
                endScenePanel.setVisible(true);
                endScenePanel.setEnabled(true);
                game.add(endScenePanel);
                gamePanel.setVisible(false);
                gamePanel.setEnabled(false);
            }

        }
    }

    //failed end scene if user fails too many times
   private class FailedEndScenePanel extends JPanel {
        private Image backgroundImage;
        private Image failedEndSceneText;
        public FailedEndScenePanel (AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(windowWidth, windowHeight));

            //button to try again
            JButton tryAgainButton = new JButton("Try again...");
            tryAgainButton.setSize(150, 50);
            tryAgainButton.setLocation(350, 450);
            tryAgainButton.setFont(new Font("Arial", Font.BOLD, 20));
            tryAgainButton.setForeground(Color.BLACK);
            tryAgainButton.setBackground(Color.WHITE);
            add(tryAgainButton);

            //if the try again button is clicked
            tryAgainButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //play the game again
                    resetGame(AdventureGame.this);
                    game.add(introductionPanel);
                    failedEndScenePanel.setVisible(false);
                    failedEndScenePanel.setEnabled(false);
                }
            });

            try { //images and text
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();
                failedEndSceneText = new ImageIcon(System.getProperty("user.dir") + "/resources/failedEndSceneText.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override //draws the images
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null && failedEndSceneText != null) {
                g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
                g.drawImage(failedEndSceneText, windowWidth / 2 - 300, windowHeight / 2 - 225, 600, 350, null);
            }

        }
        }


    //successful endScenePanel for if the user manages to complete all the checkpoints in less than 3 fails
    private class EndScenePanel extends JPanel {
        private Image backgroundImage;
        private Image endScenePanelText;

        public EndScenePanel(AdventureGame game) {
            setLayout(null);
            setPreferredSize(new Dimension(windowWidth, windowHeight));

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

            //if exit button is pressed
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //leave the game
                    System.exit(0);
                }
            });

            //if play again button is pressed
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

            try { //text and background
                backgroundImage = new ImageIcon(System.getProperty("user.dir") + "/resources/Village-Intro.png").getImage();
                endScenePanelText = new ImageIcon(System.getProperty("user.dir") + "/resources/endScenePanelText.png").getImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //draws the background image and text
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null && endScenePanelText != null) {
                g.drawImage(backgroundImage, 0, 0, windowWidth, windowHeight, null);
                g.drawImage(endScenePanelText, windowWidth / 2 - 300, windowHeight / 2 - 225, 600, 350, null);
            }

        }

        }
        
        //key handler to allow the user to use arrow keys to move around
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            //allows the music task to take input from keyboard to play the notes
            String returnMusicTask = musicTask.handleMusicTask(e.getKeyChar());
            if (returnMusicTask == "C1") { //depending on what the music task returns, will mark the task as complete
                completeTask(returnMusicTask);
            }
            else if (returnMusicTask == "WrongNote"){ //if it's the wrong note, the fail count will go up
                failureCount++;
            }
            repaint();

            if (inTask || isDialogue) { //if the user is in a task or dialogue, they can't move using arrow keys
                return;
            }

            //allows the player to move around using the arrow keys
            int keyCode = e.getKeyCode();
            switch (keyCode) { //depending on what arrow key is pressed, the user will be able to move around
                case KeyEvent.VK_LEFT: 
                    playerX = Math.max(playerX - 10, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    playerX = Math.min(playerX + 10, windowWidth - playerSize);
                    break;
                case KeyEvent.VK_UP:
                    playerY = Math.max(playerY - 10, 0);
                    break;
                case KeyEvent.VK_DOWN:
                    playerY = Math.min(playerY + 10, windowHeight - playerSize);
                    break;
            }

            //checks if the player is at a checkpoint
            for (ChoicePoint choicePoint : choicePoints) {
                if (!completedCheckpoints.contains(choicePoint.checkpoint) && choicePoint.contains(playerX, playerY)) { //if the player is at a checkpoint and the checkpoint is available (not yet completed)
                    currentCheckpoint = choicePoint.checkpoint; //sets the current checkpoint to the checkpoint the player is at
                    if (!inTask) {
                    showDialogue(currentCheckpoint); } //shows the dialogue for the checkpoint
                    break;
                }
            }

            repaint(); //resets the screen
        }
    }

    //mouse handler to take user mouse input
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {  //this checks if the player is at a checkpoint
            int x = event.getX() - getInsets().left; //this will get the x and y coordinates of the mouse click and subtract the insets (the border) from the x and y coordinates
            int y = event.getY() - getInsets().top;
            
            
            if (!inTask){ //if the user is not in a task, so the dialogue won't pop up during the task
            //for checkpoints to determine whether the player is at a checkpoint
            for (ChoicePoint choicePoint : choicePoints) {
                if (!completedCheckpoints.contains(choicePoint.checkpoint) && choicePoint.contains(x, y)) { //if the player is at a checkpoint and the checkpoint is available (not yet completed)
                    currentCheckpoint = choicePoint.checkpoint;  //set the current checkpoint to the checkpoint the player is at
                    showDialogue(currentCheckpoint); //show the dialogue for the checkpoint

                    return;
                }
            }}

            if (msgLabel.isVisible()) { //if the msg label is still visible, once clicked, it will disappear, and start the timer for hunting and gathering tasks
                msgLabel.setVisible(false);
                taskTimer = 0;
                taskTimerTimer.restart();
            }

            if (gatheringTask.inGatheringTask) { //if the player is in the gathering task
                String returnGatheringTask = gatheringTask.gatheringMouseHandler(x, y, playerSize); //call the gathering mouse handler
                if (!returnGatheringTask.isEmpty()) { //if the user has completed the task
                    completeTask(returnGatheringTask);
                }

            }
            if (huntingTask.inHuntingTask) { //if the player is in the hunting task
                String returnHuntingTask = huntingTask.huntingMouseHandler(x, y, playerSize); //call the hunting mouse handler
                if (!returnHuntingTask.isEmpty()) { //if the user has completed the task
                    completeTask(returnHuntingTask);

                }
            }

            if (barteringTask.inBarteringTask) { //if the player is in the bartering task
                String returnBarteringTask = barteringTask.barteringMouseHandler(x, y); //call the bartering mouse handler
                if (returnBarteringTask == "C4") { //if the user has completed the task
                    completeTask(returnBarteringTask);
                } else if (returnBarteringTask == "Redraw") { //if it needs to be redrawn
                    barteringTask.drawBarteringTask(getGraphics());
                }
                else  {
                    failureCount++;
                }
            }
            repaint(); //reset the screen
        }
    }

    //choicepoint class to create the different checkpoints
    private class ChoicePoint {
        int x, y; //x and y position of each choicepoint
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

        private void loadImage () { //images of each different checkpoint in the game panel
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
        
        //draw the different choicepoints with their text description
        public void draw(Graphics g) {
            //text for the description of each checkpoint (Elder, Uncle, Father, Trade Post)
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int textWidth = metrics.stringWidth(description);
            int textHeight = metrics.getHeight();

            //decalre image sizes of the icons
            int imageWidth = 100;
            int imageHeight = 100;

            //to show the text of each different checkpoint
            int textX = x + (imageWidth - textWidth) / 2;
            int textY = y + imageHeight + textHeight;

            g.setColor(Color.WHITE); //to highlight the text behind it
            g.fillRect(textX-10, textY-textHeight+2, textWidth + 20, textHeight+1);

            g.setColor(Color.BLACK); //text
            g.drawString(description, textX, textY);

            //to show the image of each different checkpoint
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

        //allows the user to enter the checkpoint if they go onto the image or the text
        public boolean contains(int pointX, int pointY) {
            // check if the user is within the image
            if (pointX >= x && pointX <= x + 50 && pointY >= y && pointY <= y + 50) {
                return true;
            }
            // checks if the user is within the text description of the image icon
            FontMetrics metrics = getGraphics().getFontMetrics();
            int textWidth = metrics.stringWidth(description);
            if (pointX >= x - 10 && pointX <= x - 10 + textWidth + 20 && pointY >= y - 30 && pointY <= y - 30 + metrics.getHeight()) {
                return true;
            }
            return false;
        }}


    //main method to run the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdventureGame game = new AdventureGame();
            game.setVisible(true);
        });
    }
    }
