package parser;

import error.LexicalError;
import error.SyntaxError;
import lexer.Lexer;
import model.unit.CompUnit;
import model.unit.IUnit;

import java.io.IOException;

public record SyntaxParser(Lexer lexer) {

    public IUnit parse() throws LexicalError, SyntaxError, IOException {
        return new CompUnit(this.lexer).build();
    }

}
