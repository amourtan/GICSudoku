package com.tansoonyoun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        var generator = new PuzzleGenerator(new Random());
        var input = new BufferedReader(new InputStreamReader(System.in));
        var output = new PrintWriter(System.out, true);
        new SudokuConsole(input, output, generator::generate).run();
    }
}
