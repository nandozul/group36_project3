

/**
 * This class stores one competitors name and match record (wins, losses, and current
 * win streak)
 * It is part of the model so it doesnt depend on the other classes, doesnt read or print anything
 * @author Megan Ochoa
 * @version 2026.11.09
 */
public class Player {
    
    public static final int Max_Name_Length = 50;
    
    private final String name;
    private int wins;
    private int losses;
    private int currentStreak;
    
    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is blank, input again");
        }
        String trimmed = name.trim();
        if (trimmed.length() > Max_Name_Length) {
            throw new IllegalArgumentException("Name is too long, the limit is 50 characters");
        }
        this.name = trimmed;
        this.wins = 0;
        this.losses = 0;
        this.currentStreak = 0;
    }
    
    public String getName() {
        return name;
    }
    public int getWins() {
        return wins;
    }
    public int getLosses() {
        return losses;
    }
    public int getCurrentStreak() {
        return currentStreak;
    }
    
    
    public void recordWin() {
        wins++;
        currentStreak++;
    }
    public void recordLoss() {
        losses++;
        currentStreak = 0;
        //reset the streak to zero when a player losses
    }
}
