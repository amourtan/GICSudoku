package com.tansoonyoun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Random;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        var generator = new PuzzleGenerator(new Random());
        Charset charset = terminalCharset();
        var input = new BufferedReader(new InputStreamReader(System.in, charset));
        // The only non-ASCII character printed is the multiplication sign in "3×3".
        var encoder = charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .replaceWith(new byte[]{'x'});
        var output = new PrintWriter(new OutputStreamWriter(System.out, encoder), true);
        new SudokuConsole(input, output, generator::generate).run();
    }

    /**
     * Returns the encoding the terminal expects, which can differ from the JVM default
     * charset (UTF-8 since Java 18) and would otherwise garble non-ASCII output.
     */
    private static Charset terminalCharset() {
        Charset charset = charsetProperty("stdout.encoding");
        if (charset == null && System.console() != null) {
            charset = System.console().charset();
        }
        if (charset == null) {
            charset = charsetProperty("native.encoding");
        }
        return charset != null ? charset : Charset.defaultCharset();
    }

    private static Charset charsetProperty(String property) {
        String name = System.getProperty(property);
        try {
            return name == null ? null : Charset.forName(name);
        } catch (IllegalArgumentException unsupported) {
            return null;
        }
    }
}
