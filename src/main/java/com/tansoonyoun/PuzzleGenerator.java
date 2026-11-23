package com.tansoonyoun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.tansoonyoun.SudokuRules.CELL_COUNT;
import static com.tansoonyoun.SudokuRules.EMPTY;

final class PuzzleGenerator {
    private static final int CLUE_COUNT = 30;
    private final Random random;

    PuzzleGenerator(Random random) {
        this.random = random;
    }

    SudokuGame generate() {
        var cells = new ArrayList<Integer>(CELL_COUNT);
        for (int cell = 0; cell < CELL_COUNT; cell++) {
            cells.add(cell);
        }

        while (true) {
            int[] solution = SudokuSolver.generateSolution(random);
            int[] clues = solution.clone();
            int remaining = CELL_COUNT;
            Collections.shuffle(cells, random);
            for (int cell : cells) {
                clues[cell] = EMPTY;
                if (SudokuSolver.countUpToTwo(clues) == 1) {
                    if (--remaining == CLUE_COUNT) {
                        return new SudokuGame(clues, solution);
                    }
                } else {
                    clues[cell] = solution[cell];
                }
            }
        }
    }
}
