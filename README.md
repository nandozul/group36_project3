# group36_project3
Group 36 Project 3

# King of the Ring

This is a "king of the hill" tournament simulator. One champion faces one challenger at a
time. The loser goes to the back of a singly linked queue and the next challenger
steps up. The cycle repeats until the user quits.

It is built on an MVC split: the model holds all state and logic and never prints, the
view only displays, and the controller mediates between them.

## Running it

The entry point is `Main`. It creates the model, view, and controller, registers the
view as a listener, and starts the main loop.

Menu: 1) Add challenger, 2) Start tournament, 3) Play next match, 4) Show
champion and queue, 5) Show leaderboard, 6) Quit.

## Classes

**Model:** `Player`, `PlayerQueue` (with private inner `Node`), `MatchResult`,
`PlayerRecord`, `Tournament`, `TournamentListener` (interface)

**View:** `ConsoleView`

**Controller:** `TournamentController`, `Main`

**Tests:** `PlayerQueueTest`, `PlayerTest`, `TournamentTest`,
`TournamentControllerTest` (54 tests, all passing)

## Reflection

### How did our design absorb the change?

Our change feature was the **leaderboard**: a menu option that lists every player
ranked by wins (then fewest losses, then name), with losses and current streak,
and the reigning champion marked with an asterisk.

**Classes the feature actually touched:**

- `Tournament` (model): new `getLeaderboard()` method that sorts the roster and
  returns an unmodifiable list.
- `PlayerRecord` (model, **new class**): a read-only snapshot of one player's
  name, wins, losses, and streak.
- `ConsoleView` (view): new `showLeaderboard()` method and a new menu line.
- `TournamentController` (controller): new `case 5` and `handleLeaderboard()`;
  quit moved from 5 to 6.
- `TournamentTest` and `TournamentControllerTest`: new tests for the feature.

**Compared with our D3 prediction:**

- **Matched:** We predicted `Tournament`, `ConsoleView`, and
  `TournamentController` would change, and they did. We predicted `PlayerQueue`,
  `Node`, `Player`, `MatchResult`, and `TournamentListener` would not change,
  and none of them did. The queue was untouched because the leaderboard reads
  the roster, not the waiting line.
- **Surprise 1, a new class:** We did not predict `PlayerRecord`. Returning
  `Player` objects to the view would have handed it `recordWin()` and
  `recordLoss()`, which would let the view change the model. A small
  read-only snapshot class kept the view from having any way to change state.
- **Surprise 2, renumbering rippled into tests:** Adding the menu option moved
  Quit from 5 to 6. Every controller test that ended its input with "5" had to
  change. The model tests were unaffected.

### Did the change cross the model/view line?

The model did not learn anything about the view: `Tournament` still prints
nothing and knows only the `TournamentListener` interface.

The view did have to learn one new thing about the model: the `PlayerRecord`
type and the `getLeaderboard()` getter. This is the same kind of read-only
dependency the view already had on `getQueueOrder()` and `getHistory()`, so the
boundary holds in the direction that matters. The view still cannot add
players, play matches, or see the queue's internals.

We did not need a new listener callback. The leaderboard is pulled on demand
when the user picks option 5, rather than pushed after every match, so
`TournamentListener` stayed the same.

The one ripple that crossed layers it ideally shouldn't have is the menu
numbering. The numbers live in two places: the view prints them and the
controller's `switch` interprets them. Adding one option meant editing both
classes and the controller tests in step.

### What changed from our D3 plan?

- **New class `PlayerRecord`:** not in the D3 prediction (see above).
- **Sort order made explicit:** The D3 test list said ties should be "stable."
  We chose a defined tiebreak instead (fewest losses, then name, ignoring case),
  so the order is the same every run and can be tested exactly.
- **Leaderboard entries are snapshots:** a list you hold does not change when
  later matches are played, matching how `getHistory()` behaves.
- **`handleAddPlayer` fixed:** D3 listed a latent trap where `handleAddPlayer`
  looped on `while (running)` and silently did nothing outside `run()`. It now
  loops until a valid name or end of input, and `testAddPlayerOutsideLoop`
  covers it.
- **Tests from the spec added:** the 100-match stress test and the listener
  notification tests promised in our specification's test plan are now in
  `TournamentTest`.

### What doesn't work yet?

- **No `MatchResultTest` or `PlayerRecordTest`.** Both are immutable data
  classes whose getters are exercised through `TournamentTest`, so they are
  covered indirectly rather than directly.
- **`ConsoleView.showMessage(String)` is never called.** It works, but no code
  path reaches it.
- **Menu numbers are duplicated** between the view and controller (see above).
- **Javadoc gaps:** `Tournament` and `TournamentListener` have no class comment
  or method comments, and `package-info.java` is empty.
- **Menu wording:** option 1 says "Add challenger" while our specification says
  "Add player."

## Next Steps

### What is left for the final build?

1. Javadoc and style pass across all files (`Tournament`, `TournamentListener`,
   `package-info.java`, and renaming `Max_Name_Length` to `MAX_NAME_LENGTH`).
2. Decide on "Add challenger" vs. "Add player" and make the spec and code agree.
3. Remove or use `showMessage(String)`.
4. Update the system diagram to show `PlayerRecord` and the read-only
   View-to-Model arrow.
5. Optional: direct unit tests for `MatchResult` and `PlayerRecord`.

### Stretch goals

**Committing to:** the leaderboard (done in this prototype) and the player
win/loss/streak records that feed it.

**Dropping:** stats-based match outcomes (strength, stamina, speed) and titles
like "3-time Champion." Stats would change only the winner selection inside
`playMatch`, and we would rather spend the remaining time on polish and tests.

### What will our 3-minute demo show?

1. Start the program and pick **5) Show leaderboard** with no players: it shows
   "(no players yet)" instead of crashing.
2. Add players, including bad input: a blank name, a 51-character name, and a
   duplicate in different capitalization. Each one is rejected and re-prompts.
3. Try **2) Start** with one player and **3) Play** before starting: both show
   an error.
4. Start the tournament and play several matches, showing the loser moving to
   the back of the queue each time.
5. Open the **leaderboard**: players ranked by wins, with the champion marked.
6. Type bad menu choices ("abc", "7") and then quit with **6** to show the
   final summary.
7. Briefly run the JUnit tests to show all 54 pass.

### Who does what?

| Remaining item | Owner |
|---|---|
| Javadoc and style pass; system diagram update | Tanvi |
| `MatchResultTest` and `PlayerRecordTest` | Zachias |
| Menu wording and `showMessage` cleanup in the view | Megan |
| Controller cleanup and demo script run-through | Fernando |

## Attribution

| File | Team member(s) | Contribution (how) |
|---|---|---|
| `Player.java` | Megan | Wrote |
| `PlayerQueue.java` | Tanvi | Drafted with GenAI assistance; reviewed by group |
| `MatchResult.java` | Megan | Drafted with GenAI assistance; reviewed by group |
| `PlayerRecord.java` | Fernando | GenAI-drafted for the leaderboard; reviewed by Megan|
| `Tournament.java` | Megan; Fernando | Megan drafted with GenAI assistance; `getLeaderboard()` GenAI-drafted, reviewed by group|
| `TournamentListener.java` | Megan | Drafted with GenAI assistance; reviewed by group |
| `ConsoleView.java` | Megan; Fernando Megan drafted with GenAI assistance; `showLeaderboard()` GenAI-drafted, reviewed by Group |
| `TournamentController.java` | Fernando | Drafted with GenAI assistance; leaderboard case and `handleAddPlayer` fix GenAI-drafted, reviewed by Fernando |
| `Main.java` | Megan | Drafted with GenAI assistance; reviewed by group |
| `package-info.java` | Megan | Drafted with GenAI assistance; reviewed by group |
| `PlayerQueueTest.java` | Tanvi | Drafted with GenAI assistance; reviewed by group |
| `PlayerTest.java` | Zachias | Drafted with GenAI assistance; reviewed by group |
| `TournamentTest.java` | Zachias; Fernando | Zachias drafted with GenAI assistance; stress, listener, and leaderboard tests GenAI-drafted, reviewed by Fernando |
| `TournamentControllerTest.java` | Fernando | Drafted with GenAI assistance; reviewed by group |
| `README.md` | Tanvi; Fernando | Drafted with GenAI assistance; reviewed by group |

### GenAI note

We used Claude throughout this deliverable. We gave it our source files and the
Deliverable 4 requirements and asked what was missing.