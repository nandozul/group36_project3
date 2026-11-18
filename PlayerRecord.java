/**
 * This class is a read-only snapshot of one player's record, used for the leaderboard.
 * It lets the view show wins, losses, and streaks without handing it a Player object,
 * which has methods that change the record.
 *
 * @author Fernando Zuniga Leon (zfernando)
 * @version 2026.11.18
 */
public final class PlayerRecord {

    private final String name;
    private final int wins;
    private final int losses;
    private final int currentStreak;

    /**
     * Creates a snapshot of the given player's current record.
     *
     * @param player the player to copy
     */
    public PlayerRecord(Player player) {
        this.name = player.getName();
        this.wins = player.getWins();
        this.losses = player.getLosses();
        this.currentStreak = player.getCurrentStreak();
    }

    /**
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return total matches won
     */
    public int getWins() {
        return wins;
    }

    /**
     * @return total matches lost
     */
    public int getLosses() {
        return losses;
    }

    /**
     * @return consecutive wins as champion
     */
    public int getCurrentStreak() {
        return currentStreak;
    }
}