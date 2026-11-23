package com.tansoonyoun;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.IntStream;

import static com.tansoonyoun.SudokuRules.CELL_COUNT;
import static org.junit.jupiter.api.Assertions.*;

class PuzzleGeneratorTest {
    @Test
    void generatesDistinctThirtyCluePuzzlesWithUniqueSolutionsAndUsableHints() {
        var generator = new PuzzleGenerator(new Random(42));
        var distinct = new HashSet<String>();
        for (int i = 0; i < 10; i++) {
            var game = generator.generate();
            int[] clues = IntStream.range(0, CELL_COUNT).map(game::valueAt).toArray();
            distinct.add(Arrays.toString(clues));
            assertEquals(30, Arrays.stream(clues).filter(digit -> digit != 0).count());
            assertEquals(1, SudokuSolver.countUpToTwo(clues));

            for (int empty = 51; empty > 0; empty--) {
                assertFalse(game.isComplete());
                var hint = game.hint().orElseThrow();
                assertEquals(0, game.valueAt(hint.cell()));
                assertEquals(SudokuGame.MoveResult.ACCEPTED, game.place(hint.cell(), hint.digit()));
                assertTrue(game.violations().isEmpty());
            }
            assertTrue(game.isComplete());
        }
        assertTrue(distinct.size() > 1);
    }

    @Test
    void solverDistinguishesZeroOneAndMultipleSolutionsWithoutMutatingInput() {
        int[] clues = ExamplePuzzle.clues();
        assertEquals(1, SudokuSolver.countUpToTwo(clues));
        assertArrayEquals(ExamplePuzzle.clues(), clues);
        assertEquals(2, SudokuSolver.countUpToTwo(new int[CELL_COUNT]));

        clues[2] = 1;
        assertTrue(SudokuRules.violations(clues).isEmpty());
        assertEquals(0, SudokuSolver.countUpToTwo(clues));
        clues[2] = 3;
        assertEquals(0, SudokuSolver.countUpToTwo(clues));
    }
}
