import java.util.List;
import java.util.Scanner;
/**
 * This class is the console view. It prints the menu, champion, queue order, match results,
 * and messages reading lines from user to call models read only getters
 *
 * @author Megan Ochoa
 * @version 2026.11.09
 */
public class ConsoleView implements TournamentListener {
    private final Tournament model;
    private final Scanner scanner;
    
    public ConsoleView(Tournament model) {
        this(model, new Scanner(System.in));
    }
    
    public ConsoleView(Tournament model, Scanner scanner) {
        this.model = model;
        this.scanner = scanner;
    }
    
    public String readLine(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!scanner.hasNextLine()) {
            System.out.println();
            return null;
        }
        return scanner.nextLine();
    }
    
    public void showMenu() {
        System.out.println();
        System.out.println("=== King of the Ring ===");
        System.out.println("1.) Add challenger");
        System.out.println("2.) Start tournament");
        System.out.println("3.) Play next match");
        System.out.println("4.) Show champion and queue");
        System.out.println("5.) Show leaderboard");
        System.out.println("6.) Quit");
    }
    
    public void showError(String message) {
        System.out.println("Error: " + message);
    }
    public void showMessage(String message) {
        System.out.println(message);
    }
    
    public void showStatus() {
        System.out.println();
        if (!model.isStarted()) {
            System.out.println("The tournament has not started.");
            System.out.println("Challengers registered (" + model.getRosterSize() + "): " + formatNames(model.getQueueOrder()));
            return;
        }
        System.out.println("Champion: " + model.getChampion());
        System.out.println("Next challenger: " + orNone(model.getNextChallenger()));
        System.out.println("Queue: " + formatNames(model.getQueueOrder()));
        System.out.println("Matches played: " + model.getMatchCount());
    }
    
    public void showSummary() {
        System.out.println();
        System.out.println("=== Final summary ===");
        if (!model.isStarted()) {
            System.out.println("The tournament has not commenced");
        }
        else {
            System.out.println("Final champion: " + model.getChampion());
            System.out.println("Matches played: " + model.getMatchCount());
            for (MatchResult r : model.getHistory()) {
                System.out.println(" " + r);
            }
        }
        System.out.println("Thanks for playing!");
    }
    
    /**
     * Prints every player ranked by wins, with losses and current streak.
     * The current champion is marked with an asterisk.
     */
    public void showLeaderboard() {
        System.out.println();
        System.out.println("=== Leaderboard ===");
        List<PlayerRecord> board = model.getLeaderboard();
        if (board.isEmpty()) {
            System.out.println("(no players yet)");
            return;
        }
        String champ = model.getChampion();
        int rank = 1;
        for (PlayerRecord r : board) {
            String mark = r.getName().equals(champ) ? " *" : "";
            System.out.println(rank + ". " + r.getName() + mark + "  W: " + r.getWins()
                + "  L: " + r.getLosses() + "  Streak: " + r.getCurrentStreak());
            rank++;
        }
        if (champ != null) {
            System.out.println("* current champion");
        }
    }

    //Tournament Listener
    
    @Override
    public void onPlayerAdded(String name) {
        System.out.println("Added " + name + " to the line");
        System.out.println("Roster (" + model.getRosterSize() + "): " + formatNames(model.getQueueOrder()));
    }
    
    @Override
    public void onTournamentStarted(String championName) {
        System.out.println("The tournament has started! " + championName + " is the first champion."); 
        showStatus();
    }
    
    @Override
    public void onMatchPlayed(MatchResult result) {
        System.out.println(result);
        showStatus();
    }
    
    
    private String formatNames(List<String> names) {
        return names.isEmpty() ? "(empty)" : String.join(" -> ", names);
    }
    
    private String orNone(String s) {
        return s == null ? "(none)" : s;
    }
}