package tasks;


public class Tasks { //this allows the tasks to be reset when the user wants to play again
    public boolean inMusicTask = false;
    public boolean inGatheringTask = false;
    public boolean inHuntingTask = false;
    public boolean inBarteringTask = false;
    public int gatherScore = 0;
    public int huntScore = 0;


    public void resetTasks() {
        inMusicTask = false;
        inGatheringTask = false;
        inHuntingTask = false;
        inBarteringTask = false;
        gatherScore = 0;
        huntScore = 0;
    }

}
