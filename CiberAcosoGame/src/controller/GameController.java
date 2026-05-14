package controller;

public class GameController {

    private static GameController instance;

    private boolean[] completed = new boolean[5];
    private int[]     stars     = new int[5];

    private GameController() {}

    public static GameController getInstance() {
        if (instance == null) instance = new GameController();
        return instance;
    }

    public void completeMission(int index, int starCount) {
        if (index < 0 || index >= 5) return;
        completed[index] = true;
        if (starCount > stars[index]) stars[index] = starCount;
    }

    public boolean isMissionCompleted(int index) {
        if (index < 0 || index >= 5) return false;
        return completed[index];
    }

    public boolean isMissionUnlocked(int index) {
        return true;
    }

    public int getStars(int index) {
        if (index < 0 || index >= 5) return 0;
        return stars[index];
    }

    public int getTotalStars() {
        int total = 0;
        for (int s : stars) total += s;
        return total;
    }

    public int getMissionsWon() {
        int count = 0;
        for (boolean c : completed) if (c) count++;
        return count;
    }
}