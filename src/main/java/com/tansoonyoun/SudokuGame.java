package com.tansoonyoun;

import java.util.List;
import java.util.Optional;

import static com.tansoonyoun.SudokuRules.*;

final class SudokuGame {
    private final int[] clues;
    private final int[] solution;
    private final int[] grid;

    SudokuGame(int[] clues, int[] solution) {
        this.clues = clues.clone();
        this.solution = solution.clone();
        grid = clues.clone();
    }

    int valueAt(int cell) {
        return grid[cell];
    }

    MoveResult place(int cell, int digit) {
        if (digit < 1 || digit > SIZE) {
            return MoveResult.INVALID_DIGIT;
        }
        return update(cell, digit);
    }

    MoveResult clear(int cell) {
        return update(cell, EMPTY);
    }

    private MoveResult update(int cell, int digit) {
        if (clues[cell] != EMPTY) {
            return MoveResult.PRE_FILLED;
        }
        grid[cell] = digit;
        return MoveResult.ACCEPTED;
    }

    List<String> violations() {
        return SudokuRules.violations(grid);
    }

    boolean isComplete() {
        for (int value : grid) {
            if (value == EMPTY) {
                return false;
            }
        }
        return violations().isEmpty();
    }

    Optional<Hint> hint() {
        for (int cell = 0; cell < CELL_COUNT; cell++) {
            if (grid[cell] == EMPTY) {
                return Optional.of(new Hint(cell, solution[cell]));
            }
        }
        for (int cell = 0; cell < CELL_COUNT; cell++) {
            if (grid[cell] != solution[cell]) {
                return Optional.of(new Hint(cell, solution[cell]));
            }
        }
        return Optional.empty();
    }

    enum MoveResult { ACCEPTED, PRE_FILLED, INVALID_DIGIT }

    record Hint(int cell, int digit) {
    }
}
