package tasks;


public class Tasks {
    public boolean inMusicTask = false;
    public boolean inGatheringTask = false;
    public boolean inHuntingTask = false;
    public boolean inBarteringTask = false;
    public int gatherScore = 0;
    public int musicScore = 0;

    public int huntScore = 0;
    public int barterScore = 0;


    public void resetTasks() {
        inMusicTask = false;
        inGatheringTask = false;
        inHuntingTask = false;
        inBarteringTask = false;
        gatherScore = 0;
        musicScore = 0;
        huntScore = 0;
        barterScore = 0;
    }

}
