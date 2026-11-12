import java.util.Random;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests the TournamentController by feeding it scripted input through a Scanner,
 * then checking the model's state afterward.
 *
 * @author Fernando Leon
 * @version 2026.11.12
 */
public class TournamentControllerTest extends TestCase {

    private Tournament model;

    /**
     * Creates a tournament with a fixed seed before each test.
     */
    public void setUp() {
        model = new Tournament(new Random(42));
    }

    /**
     * Builds a controller whose view reads from the given text.
     *
     * @param input the lines the user would type
     * @return the controller
     */
    private TournamentController controllerFor(String input) {
        ConsoleView view = new ConsoleView(model, new Scanner(input));
        model.addListener(view);
        return new TournamentController(model, view);
    }

    /**
     * Tests a normal run: add players, start, play two matches, quit.
     */
    public void testNormalRun() {
        controllerFor("1\nHawkeye\n1\nThor\n1\nIron Man\n2\n3\n3\n5\n").run();

        assertEquals(3, model.getRosterSize());
        assertTrue(model.isStarted());
        assertEquals(2, model.getMatchCount());
        assertEquals(2, model.getQueueOrder().size());
    }

    /**
     * Tests that non-numeric and out-of-range menu choices are ignored.
     */
    public void testBadMenuChoices() {
        controllerFor("abc\n\n9\n-1\n5\n").run();

        assertEquals(0, model.getRosterSize());
        assertFalse(model.isStarted());
    }

    /**
     * Tests that blank, too-long, and duplicate names re-prompt until a valid name is given.
     */
    public void testBadNamesReprompt() {
        String longName = "";
        for (int i = 0; i < 51; i++) {
            longName += "A";
        }
        controllerFor("1\nThor\n1\n   \n" + longName + "\nthor\nLoki\n5\n").run();

        assertEquals(2, model.getRosterSize());
        assertEquals("Thor", model.getQueueOrder().get(0));
        assertEquals("Loki", model.getQueueOrder().get(1));
    }

    /**
     * Tests that starting with one player and playing before starting are both rejected.
     */
    public void testStartAndPlayErrors() {
        controllerFor("3\n1\nThor\n2\n3\n5\n").run();

        assertFalse(model.isStarted());
        assertEquals(0, model.getMatchCount());
    }

    /**
     * Tests that starting twice does not change the champion.
     */
    public void testStartTwice() {
        controllerFor("1\nThor\n1\nLoki\n2\n2\n5\n").run();

        assertTrue(model.isStarted());
        assertEquals("Thor", model.getChampion());
    }

    /**
     * Tests that running out of input at the menu quits cleanly.
     */
    public void testEndOfInputAtMenu() {
        TournamentController controller = controllerFor("1\nThor\n");
        controller.run();

        assertEquals(1, model.getRosterSize());
    }

    /**
     * Tests that running out of input while entering a name quits cleanly.
     */
    public void testEndOfInputAtNamePrompt() {
        controllerFor("1\n").run();

        assertEquals(0, model.getRosterSize());
    }

    /**
     * Tests that handleChoice works on its own, outside the main loop.
     */
    public void testHandleChoiceDirectly() {
        TournamentController controller = controllerFor("");
        controller.handleChoice("4");
        controller.handleChoice("not a number");

        assertFalse(model.isStarted());
    }
}