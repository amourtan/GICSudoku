package com.tansoonyoun;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class SudokuConsoleTest {
    @Test
    void readsCommandsAndPrintsGridFeedback() throws IOException {
        String output = run("""
                a1 6
                A1 clear
                A3 10
                A0 4
                A3
                hint
                  a3   3
                CHECK
                a3 CLEAR
                check
                quit
                A3 9
                """);
        assertTrue(output.contains("Welcome to Sudoku!"));
        assertTrue(output.contains("    1 2 3 4 5 6 7 8 9"));
        assertTrue(output.contains("  A 5 3 _ _ 7 _ _ _ _"));
        assertEquals(2, output.lines().filter("Invalid move. A1 is pre-filled."::equals).count());
        assertTrue(output.contains("Invalid move. Enter a number from 1 to 9."));
        assertEquals(2, output.lines().filter(line -> line.startsWith("Invalid command.")).count());
        assertTrue(output.contains("Hint: Cell A3 = 4"));
        assertTrue(output.contains("Move accepted."));
        assertTrue(output.contains("  A 5 3 3 _ 7 _ _ _ _"));
        assertTrue(output.contains("Number 3 already exists in Row A."));
        assertTrue(output.contains("Cell cleared."));
        assertTrue(output.contains("No rule violations detected."));
        assertFalse(output.contains("  A 5 3 9 _ 7 _ _ _ _"));
    }

    @Test
    void replaysAfterCompletionAndStopsOnEndOfInput() throws IOException {
        int[] clues = ExamplePuzzle.clues();
        int[] solution = ExamplePuzzle.solution();
        var commands = new StringBuilder();
        for (int cell = 0; cell < clues.length; cell++) {
            if (clues[cell] == 0) {
                commands.append(SudokuRules.cellLabel(cell)).append(' ').append(solution[cell]).append('\n');
            }
        }
        commands.append('\n');

        String output = run(commands.toString());
        assertTrue(output.contains("  I 3 4 5 2 8 6 1 7 9"));
        assertTrue(output.contains("You have successfully completed the Sudoku puzzle!"));
        assertTrue(output.contains("Press Enter to play again, or type quit to exit."));
        assertEquals(2, output.lines().filter("Here is your puzzle:"::equals).count());
    }

    @Test
    void matchesTheTranscriptLayoutOfTheProblemStatement() throws IOException {
        String prompt = "Enter command (e.g., A3 4, C5 clear, hint, check, quit):";
        String raw = run("A3 4\nA1 6\ncheck\n");
        assertFalse(raw.replace(System.lineSeparator(), "").contains("\n"));
        String output = raw.replace(System.lineSeparator(), "\n");
        assertTrue(output.contains(prompt + "\n\nMove accepted.\n\nCurrent grid:\n    1 2 3 4 5 6 7 8 9\n"
                + "  A 5 3 4 _ 7 _ _ _ _\n"));
        assertTrue(output.contains(prompt + "\n\nInvalid move. A1 is pre-filled.\n\nCurrent grid:\n"));
        assertTrue(output.contains("  I _ _ _ _ 8 _ _ 7 9\n\n" + prompt + "\nNo rule violations detected.\n"));
    }

    private static String run(String commands) throws IOException {
        var output = new StringWriter();
        new SudokuConsole(new BufferedReader(new StringReader(commands)),
                new PrintWriter(output, true), ExamplePuzzle::game).run();
        return output.toString();
    }
}
