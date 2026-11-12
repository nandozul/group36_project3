# group36_project3
Group 36 Project 3

# King of the Ring

This is a "king of the hill" tournament simulator. One champion faces one challenger at a
time; the loser goes to the back of a singly linked queue and the next challenger
steps up. The cycle repeats until the user quits.

Built on an MVC split: the model holds all state and logic and never prints, the
view only displays, and the controller mediates between them.

## Running it

Entry point is `Main`. It creates the model, view, and controller, registers the
view as a listener, and starts the main loop.

Menu: 1) Add challenger, 2) Start tournament, 3) Play next match, 4) Show
champion and queue, 5) Quit.

## Classes

**Model** — `Player`, `PlayerQueue` (with private inner `Node`), `MatchResult`,
`Tournament`, `TournamentListener` (interface)

**View** — `ConsoleView`

**Controller** — `TournamentController`, `Main`

**Tests** — `PlayerQueueTest`, `PlayerTest`, `TournamentTest`,
`TournamentControllerTest`

## Reflection

### What changed from our specification?

**Who built what.** Section 8 of the specification assigned the model
(`Player`, `MatchResult`, `Tournament`, `TournamentListener`) to Zachias and the
view to Megan. In practice Megan wrote most of the model as well as the view and
controller, and Zachias wrote the model test suites instead. Tanvi wrote the PlayerQueue and Node part.
While Fernando 'TournamentController'. 

**Two constructors on `ConsoleView`.** The spec gave the view a single
constructor and a `Scanner` field reading `System.in`. We added a second
constructor that accepts a `Scanner`, so `TournamentControllerTest` can feed the
controller scripted input as a string instead of requiring a human at the
keyboard. This is the change that made the controller testable at all.

**Validation added that the spec did not require.** `Tournament(Random)` now
throws `IllegalArgumentException` on a null generator, and `addListener` throws
on a null listener. Neither appears in Section 5. We added them because both
would otherwise fail later with a confusing `NullPointerException` deep inside
`playMatch`.

**`getHistory()` returns a copy.** The spec said "unmodifiable list." We wrap a
fresh `ArrayList` copy so callers cannot see later matches appended to a list
they already hold.

**A package-private helper.** `Tournament.getPlayer(String)` was added for
looking up a player by name. It is not in Section 5 and nothing currently calls
it.

**Menu wording.** Option 1 reads "Add challenger" rather than the spec's "Add
player." Cosmetic, but the spec and the running program disagree.

**Error message split.** Section 6 said a non-numeric menu choice shows "Please
enter a number from 1 to 5." The controller shows "Enter a number from the menu"
for non-numeric input and keeps the spec's wording for out-of-range numbers.

### Did the model really stand alone?

Yes, no model class uses `System.out`, `System.in`,
or `Scanner`. `MatchResult.toString()` builds a string but does not print it
`ConsoleView` is what prints it. The observer pattern did its job: `Tournament`
depends only on the `TournamentListener` interface and never names a view class.

Nothing had to move across the model/view line to get it running.

One thing cuts the other way, though. `ConsoleView` holds a `Tournament` field
and calls its read-only getters directly (`isStarted`, `getChampion`,
`getQueueOrder`, `getMatchCount`, `getHistory`, `getRosterSize`). The view never
changes state and never sees `Node`, the head or tail pointers, `Random`, or the
validation rules, so the boundary holds in the direction that matters. But this
is a dependency our system diagram does not show — the diagram has no
View-to-Model arrow. The drawing and the code disagree, and the code is what we
actually built.

### What happened to our unknowns?

Our scope document listed four things we did not know how to do.

- **Tail pointer for O(1) enqueue.** Solved. `PlayerQueue` keeps both `head` and
  `tail`, so `enqueue` never walks the list. The subtlety we hit was `dequeue`:
  when the last node comes off, `tail` has to be reset to null as well, or the
  next `enqueue` attaches to a node that is no longer in the queue.
- **GUI versus console.** Resolved in the specification and confirmed in code —
  console for the MVP, GUI moved to stretch goals.
- **Weighting strength, stamina, and speed.** Still open. `playMatch` still picks
  a winner with `random.nextBoolean()`. `Player` does track `wins`, `losses`, and
  `currentStreak`, so the data a stats system would need is already being
  recorded.
- **Keeping the model from printing.** Solved, via the observer pattern. See
  above.

### What doesn't work yet?

- **Player records are tracked but never displayed.** `Player.getWins()`,
  `getLosses()`, and `getCurrentStreak()` are maintained correctly after every
  match, but no view method ever shows them. A user cannot see any player's
  record.
- **`ConsoleView.showMessage(String)` is never called.** It exists and works; no
  code path reaches it.
- **`Tournament.getPlayer(String)` is never called.** Same situation.
- **No `MatchResultTest`.** `MatchResult` is immutable with three final fields,
  and its getters are exercised through `TournamentTest.testPlayMatch`, so it is
  covered indirectly rather than directly.
- **`handleAddPlayer` depends on the main loop being active.** Its loop is
  `while (running)`, and `running` is only set true inside `run()`. Calling
  `handleChoice("1")` on a fresh controller outside the loop silently adds
  nobody. This does not affect normal use but it is a latent trap.

## Next Steps

### What is left to finish the MVP?

1. Display player records in the view, so the wins, losses, and streaks the model
   already tracks are visible to the user.
2. Remove or use the two unreachable methods (`showMessage`, `getPlayer`).
3. Reconcile the system diagram with the code, by adding the View-to-Model
   read-only arrow.
4. Fix the Javadoc and style gaps across the source files.

### Our change feature

**Feature:** Leaderboard — a new menu option that shows every player ranked by
wins, with losses and current streak, and the reigning champion marked.

**Classes we predict this touches:**

- Model: `Tournament` — a new read only method returning players ordered by
  record.
- View: `ConsoleView` — a new display method and a new menu line.
- Controller: `TournamentController` — a new case in `handleChoice` and a
  handler method.

**Classes we predict should NOT change:**

`PlayerQueue`, `Node`, `Player`, `MatchResult`, and `TournamentListener`. The
queue is unchanged because a leaderboard reads the roster, not the waiting line.
`Player` is unchanged because it already records wins, losses, and streak.
`TournamentListener` is unchanged because a leaderboard is pulled on demand
rather than pushed on a state change.

**Why:** The feature adds a new way to read existing state rather than a new kind
of state. If our model/view split is as clean as Section 3 claims, a read only
addition should not reach the queue or the data classes at all.

### Stretch goals

**Committing to:** Leaderboards, and the player win/loss records that feed them
(already implemented in `Player`).

**Dropping:** Stats based match outcomes using strength, stamina, and speed, and
titles like "3 time Champion." Stats based outcomes would change only the winner
selection inside `playMatch`, with no path for the user to reach it through the
controller or view, so it does not fit what this deliverable asks for.

### What new tests will we need?

- Leaderboard ordering, normal case: three players with different records come
  back in the right order.
- Leaderboard before the tournament starts: every player has a 0-0 record, and
  the method returns the full roster rather than throwing.
- Leaderboard on an empty roster: returns an empty result rather than throwing.
- Ties: two players with equal wins are both present and ordering is stable.
- Returned list is unmodifiable, matching `getQueueOrder` and `getHistory`.
- Controller: choosing the new menu option does not change roster size, match
  count, or champion.

### Who does what?

| Remaining item | Owner |
|---|---|
| Leaderboard model method and its unit tests | Zachias |
| Leaderboard display and menu line | Megan |
| Controller case, handler, and controller tests | Fernando |
| System diagram update; Javadoc and style pass | Tanvi |

## Attribution

| File | Team member(s) | Contribution (how) |
|---|---|---|
| `Player.java` | Megan | Wrote |
| `PlayerQueue.java` | Tanvi |  Drafted with GenAI assistance; reviewed by group |
| `MatchResult.java` | Megan |  Drafted with GenAI assistance; reviewed by group |
| `Tournament.java` | Megan |  Drafted with GenAI assistance; reviewed by group |
| `TournamentListener.java` | Megan |  Drafted with GenAI assistance; reviewed by group |
| `ConsoleView.java` | Megan |  Drafted with GenAI assistance; reviewed by group |
| `TournamentController.java` | Fernando |  Drafted with GenAI assistance; reviewed by group |
| `Main.java` | Megan |  Drafted with GenAI assistance; reviewed by group |
| `package-info.java` | Megan |  Drafted with GenAI assistance; reviewed by group |
| `PlayerQueueTest.java` | Tanvi |  Drafted with GenAI assistance; reviewed by group |
| `PlayerTest.java` | Zachias |  Drafted with GenAI assistance; reviewed by group |
| `TournamentTest.java` | Zachias |  Drafted with GenAI assistance; reviewed by group |
| `TournamentControllerTest.java` | Fernando |  Drafted with GenAI assistance; reviewed by group |
| `README.md` | Tanvi | Drafted with GenAI assistance; reviewed by group |

### GenAI note

For this deliverable, we gave it our drafted files and the
rubric, and asked it fix what was wrong, we changed up comments
to make sure we better understood what it told us. 
It fixed up our files and helped us understand the goal at hand better.
