
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
}

