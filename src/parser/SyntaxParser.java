package parser;

import error.CompileError;
import error.LexicalError;
import error.SyntaxError;
import model.unit.CompUnit;
import model.unit.IUnit;

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
