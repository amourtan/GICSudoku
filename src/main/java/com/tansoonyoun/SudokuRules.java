package com.tansoonyoun;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

final class SudokuRules {
    static final int SIZE = 9;
    static final int BOX_SIZE = 3;
    static final int CELL_COUNT = SIZE * SIZE;
    static final int EMPTY = 0;

    private SudokuRules() {
    }

    static int box(int row, int column) {
        return row / BOX_SIZE * BOX_SIZE + column / BOX_SIZE;
    }

    static char rowLabel(int row) {
        return (char) ('A' + row);
    }

    static String cellLabel(int cell) {
        return "" + rowLabel(cell / SIZE) + (cell % SIZE + 1);
    }

    static List<String> violations(int[] grid) {
        var violations = new ArrayList<String>();
        for (int row = 0; row < SIZE; row++) {
            int start = row * SIZE;
            char label = rowLabel(row);
            reportDuplicates(grid, offset -> start + offset,
                    digit -> violations.add("Number " + digit + " already exists in Row " + label + "."));
        }
        for (int column = 0; column < SIZE; column++) {
            int start = column;
            reportDuplicates(grid, offset -> start + offset * SIZE,
                    digit -> violations.add("Number " + digit + " already exists in Column " + (start + 1) + "."));
        }
        for (int box = 0; box < SIZE; box++) {
            int start = box / BOX_SIZE * BOX_SIZE * SIZE + box % BOX_SIZE * BOX_SIZE;
            reportDuplicates(grid, offset -> start + offset / BOX_SIZE * SIZE + offset % BOX_SIZE,
                    digit -> violations.add("Number " + digit + " already exists in the same 3\u00d73 subgrid."));
        }
        return violations;
    }

    private static void reportDuplicates(int[] grid, IntUnaryOperator cellAt, IntConsumer report) {
        int seen = 0;
        int reported = 0;
        for (int offset = 0; offset < SIZE; offset++) {
            int digit = grid[cellAt.applyAsInt(offset)];
            if (digit == EMPTY) {
                continue;
            }
            int bit = 1 << digit;
            if ((seen & bit) != 0 && (reported & bit) == 0) {
                report.accept(digit);
                reported |= bit;
            }
            seen |= bit;
        }
    }
}
