package com.tansoonyoun;

final class ExamplePuzzle {
    private ExamplePuzzle() {
    }

    static int[] clues() {
        return digits("""
                530070000
                600195000
                098000060
                800060003
                400803001
                700020006
                060000280
                000419005
                000080079
                """);
    }

    static int[] solution() {
        return digits("""
                534678912
                672195348
                198342567
                859761423
                426853791
                713924856
                961537284
                287419635
                345286179
                """);
    }

    static SudokuGame game() {
        return new SudokuGame(clues(), solution());
    }

    private static int[] digits(String rows) {
        return rows.chars().filter(Character::isDigit).map(c -> c - '0').toArray();
    }
}
