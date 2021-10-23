package parser;

import error.CompileError;
import model.unit.CompUnit;

import java.io.IOException;

public class SyntaxParser {

    private static final SyntaxParser parser = new SyntaxParser();

    public static SyntaxParser getParser() {
        return parser;
    }

    private SyntaxParser() { }

    public CompUnit parse() throws IOException, CompileError {
        return new CompUnit().build();
    }

}
