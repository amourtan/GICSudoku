package com.tansoonyoun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.function.Supplier;

import static com.tansoonyoun.SudokuRules.*;

final class SudokuConsole {
    private final BufferedReader input;
    private final PrintWriter output;
    private final Supplier<SudokuGame> newGame;

    SudokuConsole(BufferedReader input, PrintWriter output, Supplier<SudokuGame> newGame) {
        this.input = input;
        this.output = output;
        this.newGame = newGame;
    }

    void run() throws IOException {
        output.println("Welcome to Sudoku!");
        while (play(newGame.get())) {
            output.println("Press Enter to play again, or type quit to exit.");
            String reply = input.readLine();
            if (reply == null || reply.strip().equalsIgnoreCase("quit")) {
                return;
            }
        }
    }

    private boolean play(SudokuGame game) throws IOException {
        printGrid("Here is your puzzle:", game);
        while (true) {
            output.println("\nEnter command (e.g., A3 4, C5 clear, hint, check, quit):");
            String line = input.readLine();
            if (line == null) {
                return false;
            }
            String command = line.strip().toLowerCase(Locale.ROOT);
            switch (command) {
                case "quit" -> {
                    return false;
                }
                case "check" -> check(game);
                case "hint" -> game.hint().ifPresentOrElse(
                        hint -> output.println("Hint: Cell " + cellLabel(hint.cell()) + " = " + hint.digit()),
                        () -> output.println("No hint needed."));
                default -> {
                    if (edit(command, game) && game.isComplete()) {
                        output.println("\nYou have successfully completed the Sudoku puzzle!");
                        return true;
                    }
                }
            }
        }
    }

    private void check(SudokuGame game) {
        var violations = game.violations();
        if (violations.isEmpty()) {
            output.println("No rule violations detected.");
        } else {
            violations.forEach(output::println);
        }
    }

    private boolean edit(String command, SudokuGame game) {
        String[] tokens = command.split("\\s+");
        if (tokens.length != 2 || !tokens[0].matches("[a-i][1-9]")) {
            output.println("Invalid command. Use a cell from A1 to I9, hint, check, or quit.");
            return false;
        }
        int cell = (tokens[0].charAt(0) - 'a') * SIZE + tokens[0].charAt(1) - '1';
        String value = tokens[1];
        boolean clear = value.equals("clear");
        SudokuGame.MoveResult result = clear ? game.clear(cell)
                : game.place(cell, value.matches("[1-9]") ? value.charAt(0) - '0' : EMPTY);
        switch (result) {
            case ACCEPTED -> output.println(clear ? "Cell cleared." : "Move accepted.");
            case PRE_FILLED -> output.println("Invalid move. " + cellLabel(cell) + " is pre-filled.");
            case INVALID_DIGIT -> output.println("Invalid move. Enter a number from 1 to 9.");
        }
        printGrid("Current grid:", game);
        return result == SudokuGame.MoveResult.ACCEPTED;
    }

    private void printGrid(String title, SudokuGame game) {
        output.println();
        output.println(title);
        output.println("    1 2 3 4 5 6 7 8 9");
        for (int row = 0; row < SIZE; row++) {
            var line = new StringBuilder("  ").append(rowLabel(row));
            for (int column = 0; column < SIZE; column++) {
                int digit = game.valueAt(row * SIZE + column);
                line.append(' ').append(digit == EMPTY ? '_' : (char) ('0' + digit));
            }
            output.println(line);
        }
    }
}
