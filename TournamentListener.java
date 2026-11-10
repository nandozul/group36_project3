
public interface TournamentListener {
    
    void onPlayerAdded(String name);
    
    void onTournamentStarted(String championName);
    
    void onMatchPlayed(MatchResult result);
    
}
