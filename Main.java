import java.util.Random;
/**
 * MAIN 
 * 
 * @author Megan Ochoa
 * @version 2026.11.10
 */
public class Main {
    public static void main(String[] args) {
        Tournament model = new Tournament(new Random());
        ConsoleView view = new ConsoleView(model);
        model.addListener(view);
        TournamentController controller = new TournamentController(model, view);
        controller.run();
    }
}
