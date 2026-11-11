import student.TestCase;

/**
 * Tests the Player class and its validation rules.
 *
 * @author Zachias Noble
 * @version 2026.11.11
 */
public class PlayerTest extends student.TestCase {

    private Player player;

    /**
     * Creates a player before each test.
     */
    public void setUp() {
        player = new Player("Hawkeye");
    }

    /**
     * Tests the initial player information.
     */
    public void testPlayer() {
        assertEquals("Hawkeye", player.getName());
        assertEquals(0, player.getWins());
        assertEquals(0, player.getLosses());
        assertEquals(0, player.getCurrentStreak());
    }

    /**
     * Tests that names are trimmed.
     */
    public void testTrimName() {
        Player p = new Player("  Iron Man  ");
        assertEquals("Iron Man", p.getName());
    }

    /**
     * Tests invalid blank names.
     */
    public void testBlankName() {
        try {
            new Player("   ");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests null names.
     */
    public void testNullName() {
        try {
            new Player(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests names longer than 50 characters.
     */
    public void testLongName() {
        String name = "";

        for (int i = 0; i < 51; i++) {
            name += "A";
        }

        try {
            new Player(name);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests a valid 50-character name.
     */
    public void testNameLengthBoundary() {
        String name = "";

        for (int i = 0; i < 50; i++) {
            name += "A";
        }

        Player p = new Player(name);
        assertEquals(50, p.getName().length());
    }

    /**
     * Tests wins and winning streaks.
     */
    public void testRecordWin() {
        player.recordWin();
        player.recordWin();

        assertEquals(2, player.getWins());
        assertEquals(2, player.getCurrentStreak());
    }

    /**
     * Tests losses and resetting the streak.
     */
    public void testRecordLoss() {
        player.recordWin();
        player.recordWin();
        player.recordLoss();

        assertEquals(2, player.getWins());
        assertEquals(1, player.getLosses());
        assertEquals(0, player.getCurrentStreak());
    }
}

