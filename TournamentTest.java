import java.util.List;
import java.util.Random;
import student.TestCase;

/**
 * Tests tournament state and validation.
 *
 * @author Zachias Noble
 * @version 2026.11.11
 */
public class TournamentTest extends TestCase {

    private Tournament tournament;

    /**
     * Creates a tournament before each test.
     */
    public void setUp() {
        tournament = new Tournament(new Random(42));
    }

    /**
     * Tests the initial tournament state.
     */
    public void testInitialState() {
        assertFalse(tournament.isStarted());
        assertNull(tournament.getChampion());
        assertEquals(0, tournament.getRosterSize());
        assertEquals(0, tournament.getMatchCount());
        assertEquals(0, tournament.getQueueOrder().size());
    }

    /**
     * Tests adding players.
     */
    public void testAddPlayer() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Iron Man");

        assertEquals(2, tournament.getRosterSize());
        assertEquals("Hawkeye",
            tournament.getQueueOrder().get(0));
        assertEquals("Iron Man",
            tournament.getQueueOrder().get(1));
    }

    /**
     * Tests duplicate names ignoring case and spaces.
     */
    public void testDuplicateName() {
        tournament.addPlayer("Iron Man");

        try {
            tournament.addPlayer("  iron man  ");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        assertEquals(1, tournament.getRosterSize());
        assertEquals(1, tournament.getQueueOrder().size());
    }

    /**
     * Tests starting with too few players.
     */
    public void testStartTooFewPlayers() {
        tournament.addPlayer("Hawkeye");

        try {
            tournament.startTournament();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        assertFalse(tournament.isStarted());
    }

    /**
     * Tests starting a tournament normally.
     */
    public void testStartTournament() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Iron Man");
        tournament.addPlayer("Thor");

        tournament.startTournament();

        assertTrue(tournament.isStarted());
        assertEquals("Hawkeye", tournament.getChampion());
        assertEquals(2, tournament.getQueueOrder().size());
        assertEquals("Iron Man",
            tournament.getNextChallenger());
    }

    /**
     * Tests starting the tournament twice.
     */
    public void testStartTwice() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Thor");
        tournament.startTournament();

        try {
            tournament.startTournament();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests playing before the tournament starts.
     */
    public void testPlayBeforeStart() {
        try {
            tournament.playMatch();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        assertEquals(0, tournament.getMatchCount());
    }

    /**
     * Tests that a match updates tournament state.
     */
    public void testPlayMatch() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Iron Man");
        tournament.addPlayer("Thor");

        tournament.startTournament();

        MatchResult result = tournament.playMatch();

        assertEquals(1, tournament.getMatchCount());
        assertEquals(1, tournament.getHistory().size());
        assertEquals(1, result.getMatchNumber());
        assertEquals(result.getWinnerName(),
            tournament.getChampion());

        assertEquals(2, tournament.getQueueOrder().size());
        assertEquals(result.getLoserName(),
            tournament.getQueueOrder().get(1));
    }

    /**
     * Tests that queue order cannot be modified externally.
     */
    public void testQueueUnmodifiable() {
        tournament.addPlayer("Hawkeye");

        try {
            tournament.getQueueOrder().add("Thor");
            fail("Expected UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e) {
            assertEquals(1, tournament.getRosterSize());
        }
    }

    /**
     * Tests a null random generator.
     */
    public void testNullRandom() {
        try {
            new Tournament(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests that 100 matches keep the queue and records consistent.
     */
    public void testManyMatches() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Iron Man");
        tournament.addPlayer("Thor");
        tournament.addPlayer("Spider-Man");
        tournament.startTournament();

        for (int i = 0; i < 100; i++) {
            tournament.playMatch();
            assertEquals(3, tournament.getQueueOrder().size());
        }

        assertEquals(100, tournament.getMatchCount());
        assertEquals(100, tournament.getHistory().size());

        int totalWins = 0;
        int totalLosses = 0;
        String[] names = {"Hawkeye", "Iron Man", "Thor", "Spider-Man"};
        for (String name : names) {
            Player p = tournament.getPlayer(name);
            totalWins += p.getWins();
            totalLosses += p.getLosses();
        }
        assertEquals(100, totalWins);
        assertEquals(100, totalLosses);
    }

    /**
     * Tests that listeners are notified once for each successful change.
     */
    public void testListenerNotified() {
        CountingListener listener = new CountingListener();
        tournament.addListener(listener);

        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Thor");
        tournament.startTournament();
        MatchResult result = tournament.playMatch();

        assertEquals(2, listener.added);
        assertEquals(1, listener.started);
        assertEquals(1, listener.matches);
        assertEquals(result, listener.lastResult);
    }

    /**
     * Tests that failed actions do not notify listeners.
     */
    public void testListenerNotNotifiedOnFailure() {
        CountingListener listener = new CountingListener();
        tournament.addListener(listener);
        tournament.addPlayer("Iron Man");

        try {
            tournament.addPlayer("iron man");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertEquals(1, listener.added);
        }

        try {
            tournament.startTournament();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e) {
            assertEquals(0, listener.started);
        }
    }

    /**
     * Tests adding a null listener.
     */
    public void testNullListener() {
        try {
            tournament.addListener(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests leaderboard ordering after matches: wins first, then fewest losses.
     */
    public void testLeaderboardOrder() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Iron Man");
        tournament.addPlayer("Thor");
        tournament.startTournament();
        for (int i = 0; i < 20; i++) {
            tournament.playMatch();
        }

        List<PlayerRecord> board = tournament.getLeaderboard();
        assertEquals(3, board.size());
        for (int i = 0; i < board.size() - 1; i++) {
            PlayerRecord a = board.get(i);
            PlayerRecord b = board.get(i + 1);
            assertTrue(a.getWins() > b.getWins()
                || (a.getWins() == b.getWins() && a.getLosses() <= b.getLosses()));
        }
    }

    /**
     * Tests that the leaderboard matches each player's real record.
     */
    public void testLeaderboardMatchesRecords() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Thor");
        tournament.startTournament();
        tournament.playMatch();

        for (PlayerRecord r : tournament.getLeaderboard()) {
            Player p = tournament.getPlayer(r.getName());
            assertEquals(p.getWins(), r.getWins());
            assertEquals(p.getLosses(), r.getLosses());
            assertEquals(p.getCurrentStreak(), r.getCurrentStreak());
        }
        assertEquals(1, tournament.getLeaderboard().get(0).getWins());
    }

    /**
     * Tests the leaderboard before the tournament starts: everyone is 0-0,
     * ties are broken alphabetically, and nothing is thrown.
     */
    public void testLeaderboardBeforeStart() {
        tournament.addPlayer("Thor");
        tournament.addPlayer("hawkeye");
        tournament.addPlayer("Iron Man");

        List<PlayerRecord> board = tournament.getLeaderboard();
        assertEquals(3, board.size());
        assertEquals("hawkeye", board.get(0).getName());
        assertEquals("Iron Man", board.get(1).getName());
        assertEquals("Thor", board.get(2).getName());
        assertEquals(0, board.get(0).getWins());
    }

    /**
     * Tests the leaderboard on an empty roster returns an empty list.
     */
    public void testLeaderboardEmpty() {
        List<PlayerRecord> board = tournament.getLeaderboard();

        assertNotNull(board);
        assertTrue(board.isEmpty());
    }

    /**
     * Tests that the leaderboard cannot be modified from outside.
     */
    public void testLeaderboardUnmodifiable() {
        tournament.addPlayer("Thor");

        try {
            tournament.getLeaderboard().clear();
            fail("Expected UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e) {
            assertEquals(1, tournament.getLeaderboard().size());
        }
    }

    /**
     * Tests that leaderboard entries are snapshots: later matches do not change them.
     */
    public void testLeaderboardIsSnapshot() {
        tournament.addPlayer("Hawkeye");
        tournament.addPlayer("Thor");
        tournament.startTournament();

        List<PlayerRecord> before = tournament.getLeaderboard();
        tournament.playMatch();

        assertEquals(0, before.get(0).getWins());
        assertEquals(0, before.get(1).getWins());
    }

    /**
     * A test listener that counts how many times each callback is called.
     */
    private static class CountingListener implements TournamentListener {
        private int added;
        private int started;
        private int matches;
        private MatchResult lastResult;

        @Override
        public void onPlayerAdded(String name) {
            added++;
        }

        @Override
        public void onTournamentStarted(String championName) {
            started++;
        }

        @Override
        public void onMatchPlayed(MatchResult result) {
            matches++;
            lastResult = result;
        }
    }
}