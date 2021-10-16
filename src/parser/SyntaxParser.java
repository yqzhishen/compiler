package parser;

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

    public IUnit parse() throws LexicalError, SyntaxError, IOException {
        return new CompUnit().build();
    }

}
