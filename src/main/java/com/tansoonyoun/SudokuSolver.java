package com.tansoonyoun;

import java.util.Random;

import static com.tansoonyoun.SudokuRules.*;

final class SudokuSolver {
    private static final int ALL_DIGITS = (1 << (SIZE + 1)) - 2;

    private SudokuSolver() {
    }

    static int[] generateSolution(Random random) {
        int[] grid = new int[CELL_COUNT];
        if (new Search(grid, random).visit(1) != 1) {
            throw new IllegalStateException("Unable to solve an empty Sudoku grid");
        }
        return grid;
    }

    static int countUpToTwo(int[] grid) {
        if (!violations(grid).isEmpty()) {
            return 0;
        }
        return new Search(grid.clone(), null).visit(2);
    }

    private static final class Search {
        private final int[] grid;
        private final Random random;
        private final int[] rows = new int[SIZE];
        private final int[] columns = new int[SIZE];
        private final int[] boxes = new int[SIZE];

        private Search(int[] grid, Random random) {
            this.grid = grid;
            this.random = random;
            for (int cell = 0; cell < CELL_COUNT; cell++) {
                if (grid[cell] != EMPTY) {
                    toggle(cell, 1 << grid[cell]);
                }
            }
        }

        private int visit(int limit) {
            int chosen = -1;
            int choices = 0;
            int fewest = SIZE + 1;
            for (int cell = 0; cell < CELL_COUNT; cell++) {
                if (grid[cell] != EMPTY) {
                    continue;
                }
                int available = candidates(cell);
                int count = Integer.bitCount(available);
                if (count == 0) {
                    return 0;
                }
                if (count < fewest) {
                    chosen = cell;
                    choices = available;
                    fewest = count;
                }
            }
            if (chosen == -1) {
                return 1;
            }

            int found = 0;
            while (choices != 0) {
                int bit = pick(choices);
                choices ^= bit;
                grid[chosen] = Integer.numberOfTrailingZeros(bit);
                toggle(chosen, bit);
                found += visit(limit - found);
                // Generation keeps the first solution; counting must backtrack.
                if (random != null && found != 0) {
                    return found;
                }
                toggle(chosen, bit);
                grid[chosen] = EMPTY;
                if (found == limit) {
                    break;
                }
            }
            return found;
        }

        private int candidates(int cell) {
            int row = cell / SIZE;
            int column = cell % SIZE;
            return ALL_DIGITS & ~(rows[row] | columns[column] | boxes[box(row, column)]);
        }

        private int pick(int choices) {
            if (random != null) {
                int skip = random.nextInt(Integer.bitCount(choices));
                for (int i = 0; i < skip; i++) {
                    choices &= choices - 1;
                }
            }
            return Integer.lowestOneBit(choices);
        }

        private void toggle(int cell, int bit) {
            int row = cell / SIZE;
            int column = cell % SIZE;
            rows[row] ^= bit;
            columns[column] ^= bit;
            boxes[box(row, column)] ^= bit;
        }
    }
}
