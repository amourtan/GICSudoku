# Command-line Sudoku

Play a randomly generated 9×9 Sudoku with exactly 30 pre-filled cells and one solution.

## Build and run

Install JDK 17 or newer and Apache Maven 3.8+ on Windows, macOS, or Linux. Make sure `java` and `mvn` are on your PATH. Maven needs internet access on the first build to download plugins and test dependencies; the game itself runs offline.

From the project root, build and run:

```text
mvn clean package
java -jar target/GICSudoku-1.0-SNAPSHOT.jar
```

Run `mvn test` to run just the test suite. In IntelliJ IDEA, you can also open `pom.xml` as a Maven project and run `com.tansoonyoun.Main`.

## How to play

Rows are labeled `A`–`I`, columns `1`–`9`, and `_` marks an empty cell. Commands are case-insensitive; press Enter to submit them.

| Command | Action |
| --- | --- |
| `B3 7` | Put 7 in B3, replacing a previous entry if present. |
| `C5 clear` | Empty C5. Pre-filled cells cannot be edited or cleared. |
| `hint` | Reveal the correct digit for the first empty cell without changing the grid. If the board is full but incorrect, reveal a correction instead. |
| `check` | Report duplicate digits in rows, columns, and 3×3 boxes, or confirm there are no rule violations. If two cells violate multiple rules, each violation is reported. An incomplete board can pass this check. |
| `quit` | Exit the game. End of input also exits. |

Conflicting digits in editable cells are accepted so you can work through a puzzle before running `check`. The round ends automatically when the grid is full and has no duplicates. Press Enter to start a new puzzle or enter `quit` to exit.

## Design

The console handles line input and display. `SudokuGame` protects the original clues and manages the player's entries; `SudokuRules` checks for duplicates. `PuzzleGenerator` creates a randomized solved grid, then removes clues in shuffled order. Backtracking verifies uniqueness after each removal and stops counting at two solutions. If one removal pass cannot reach exactly 30 clues, the generator starts over. There is no difficulty setting or external Sudoku library.

The JUnit suite covers move validation, duplicate reporting, hints, the win condition, generation, and scripted console sessions.
