/**
 * Recording matchsss
 * 
 *  @author Megan Ochoa
 *  @version 2026.11.10
 */
public final class MatchResult {
    
    private final int matchNumber;
    private final String winnerName;
    private final String loserName;
    
    public MatchResult(int matchNumber, String winnerName, String loserName) {
        this.matchNumber = matchNumber;
        this.winnerName = winnerName;
        this.loserName = loserName;
    }
    
    public int getMatchNumber() {
        return matchNumber;
    }
    
    public String getWinnerName() {
        return winnerName;
    }
    
    public String getLoserName() {
        return loserName;
    }
    
    @Override
    public String toString() {
        return "Match " + matchNumber + ": " + winnerName + " annihilated " + loserName;
    }
}
