
/**
 * This class runs the main loop. It asks the view for input, parses menu choices, calls the
 * model, and catches model exceptions 
 * @author Megan Ochoa
 * @version 2026.11.10
 */
public class TournamentController {
    
    private final Tournament model;
    private final ConsoleView view;
    private boolean running;
    
    public TournamentController(Tournament model, ConsoleView view) {
        this.model = model;
        this.view = view;
        this.running = false;
    }
    
    public void run() {
        running = true;
        while (running) {
            view.showMenu();
            String input = view.readLine("Choose an option: ");
            if (input == null) {
                handleQuit();
            }
            else {
                handleChoice(input);
            }
        }
    }
    
    public void handleChoice(String input) {
        int choice;
        try {
            choice = Integer.parseInt(input.trim());
        }
        catch (NumberFormatException e) {
            view.showError("Enter a number from the menu");
            return;
        }
        
        switch (choice) {
            case 1:
                handleAddPlayer();
                break;
            case 2:
                handleStart();
                break;
            case 3:
                handlePlayMatch();
                break;
            case 4:
                view.showStatus();
                break;
            case 5:
                handleQuit();
                break;
            default:
                view.showError("Please enter a number from 1 to 5");
        }
    }
    
    public void handleAddPlayer() {
        while (running) {
            String name = view.readLine("Player name: ");
            if (name == null) {    // end of input
                handleQuit();
                return;
            }
            try {
                model.addPlayer(name);
                return;
            }
            catch (IllegalArgumentException e) {
                view.showError(e.getMessage());
            }
        }
    }
    
    public void handleStart() {
        try {
            model.startTournament();
        }
        catch (IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }
    
    public void handlePlayMatch() {
        try {
            model.playMatch();
        }
        catch (IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }
    
    public void handleQuit() {
        running = false;
        view.showSummary();
    }
}
