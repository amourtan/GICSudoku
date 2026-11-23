package com.tansoonyoun;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static com.tansoonyoun.SudokuGame.MoveResult.*;
import static org.junit.jupiter.api.Assertions.*;

class SudokuGameTest {
    @Test
    void cluesCannotBeChangedOrCleared() {
        var game = ExamplePuzzle.game();
        assertEquals(PRE_FILLED, game.place(0, 6));
        assertEquals(PRE_FILLED, game.clear(0));
        assertEquals(5, game.valueAt(0));
    }

    @Test
    void editableCellSupportsPlacementReplacementAndClearButRejectsOutOfRangeDigits() {
        var game = ExamplePuzzle.game();
        assertEquals(ACCEPTED, game.place(2, 9));
        assertEquals(ACCEPTED, game.place(2, 1));
        assertEquals(INVALID_DIGIT, game.place(2, 0));
        assertEquals(INVALID_DIGIT, game.place(2, 10));
        assertEquals(1, game.valueAt(2));
        assertEquals(ACCEPTED, game.clear(2));
        assertEquals(0, game.valueAt(2));
    }

    @ParameterizedTest
    @CsvSource({
            "2, 3, Number 3 already exists in Row A.",
            "18, 5, Number 5 already exists in Column 1.",
            "11, 8, Number 8 already exists in the same 3\u00d73 subgrid."
    })
    void conflictingEntriesAreAcceptedAndReported(int cell, int digit, String violation) {
        var game = ExamplePuzzle.game();
        assertTrue(game.violations().isEmpty());
        assertEquals(ACCEPTED, game.place(cell, digit));
        assertTrue(game.violations().contains(violation));
        game.clear(cell);
        assertTrue(game.violations().isEmpty());
    }

    @Test
    void reportsBothViolationsWhenTwoCellsShareAColumnAndABox() {
        var game = ExamplePuzzle.game();
        game.place(11, 8);
        assertEquals(List.of("Number 8 already exists in Column 3.",
                "Number 8 already exists in the same 3\u00d73 subgrid."), game.violations());
    }

    @Test
    void onlyAValidFullGridWinsAndHintsDoNotChangeTheGrid() {
        var game = ExamplePuzzle.game();
        assertFalse(game.isComplete());
        assertEquals(new SudokuGame.Hint(2, 4), game.hint().orElseThrow());
        assertEquals(0, game.valueAt(2));

        int[] solution = ExamplePuzzle.solution();
        for (int cell = 0; cell < solution.length; cell++) {
            if (game.valueAt(cell) == 0) {
                game.place(cell, solution[cell]);
            }
        }
        game.place(2, 3);
        assertFalse(game.isComplete());
        assertEquals(new SudokuGame.Hint(2, 4), game.hint().orElseThrow());
        assertEquals(3, game.valueAt(2));
        game.place(2, 4);
        assertTrue(game.isComplete());
        assertTrue(game.hint().isEmpty());
    }
}
