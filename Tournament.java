import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class Tournament {
  
    private final Map<String, Player> roster;
    private final PlayerQueue waiting;
    private Player champion;
    private boolean started;
    private int matchCount;
    private final List<MatchResult> history;
    private final Random random;
    private final List<TournamentListener> listeners;
    
    
    public Tournament(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random generator cannot be null");
        }
        this.roster = new HashMap<String, Player>();
        this.waiting = new PlayerQueue();
        this.champion = null;
        this.started = false;
        this.matchCount = 0;
        this.history = new ArrayList<MatchResult>();
        this.random = random;
        this.listeners = new ArrayList<TournamentListener>();
    }
 
    public void addPlayer(String name) {
        Player player = new Player(name); 
        String key = player.getName().toLowerCase();
        if (roster.containsKey(key)) {
            throw new IllegalArgumentException("That name is already taken, input again");
        }
        roster.put(key, player);
        waiting.enqueue(player);
        for (TournamentListener l : listeners) {
            l.onPlayerAdded(player.getName());
        }
    }

    public void startTournament() {
        if (started) {
            throw new IllegalStateException("The tournament has already started");
        }
        if (roster.size() < 2) {
            throw new IllegalStateException("At least two players are required to start");
        }
        champion = waiting.dequeue();
        started = true;
        for (TournamentListener l : listeners) {
            l.onTournamentStarted(champion.getName());
        }
    }
 
    public MatchResult playMatch() {
        if (!started) {
            throw new IllegalStateException("Start the tournament first");
        }
        Player challenger = waiting.dequeue();
        Player winner;
        Player loser;
        if (random.nextBoolean()) {
            winner = champion;
            loser = challenger;
        }
        else {
            winner = challenger;
            loser = champion;
        }
        winner.recordWin();
        loser.recordLoss();
        champion = winner;
        waiting.enqueue(loser);
 
        matchCount++;
        MatchResult result = new MatchResult(matchCount, winner.getName(), loser.getName());
        history.add(result);
        for (TournamentListener l : listeners) {
            l.onMatchPlayed(result);
        }
        return result;
    }
 
    public String getChampion() {
        return champion == null ? null : champion.getName();
    }
 
    public String getNextChallenger() {
        return waiting.isEmpty() ? null : waiting.peek().getName();
    }
 
    public List<String> getQueueOrder() {
        return Collections.unmodifiableList(waiting.toNameList());
    }
 
    public int getMatchCount() {
        return matchCount;
    }

    public List<MatchResult> getHistory() {
        return Collections.unmodifiableList(new ArrayList<MatchResult>(history));
    }
 
    public int getRosterSize() {
        return roster.size();
    }

    public boolean isStarted() {
        return started;
    }
 
    public void addListener(TournamentListener l) {
        if (l == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.add(l);
    }
 
    /**
     * Returns every registered player ranked by record: most wins first, then fewest
     * losses, then name in alphabetical order (ignoring case) to break ties.
     * Works before the tournament starts (everyone is 0-0) and on an empty roster.
     *
     * @return an unmodifiable list of read-only player records, best first
     */
    public List<PlayerRecord> getLeaderboard() {
        List<PlayerRecord> board = new ArrayList<PlayerRecord>();
        for (Player p : roster.values()) {
            board.add(new PlayerRecord(p));
        }
        Collections.sort(board, new Comparator<PlayerRecord>() {
            @Override
            public int compare(PlayerRecord a, PlayerRecord b) {
                if (a.getWins() != b.getWins()) {
                    return b.getWins() - a.getWins();
                }
                if (a.getLosses() != b.getLosses()) {
                    return a.getLosses() - b.getLosses();
                }
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        return Collections.unmodifiableList(board);
    }

    Player getPlayer(String name) {
        return roster.get(name.trim().toLowerCase());
    }
    
}