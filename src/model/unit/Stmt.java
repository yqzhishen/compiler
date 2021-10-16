package model.unit;

import error.LexicalError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.TokenType;

import java.io.IOException;

public class Stmt extends AbstractUnit {

    public Stmt(Lexer lexer) {
        super(lexer);
    }

    @Override
    public IUnit build() throws LexicalError, SyntaxError, IOException {
        this.append(TokenType.Return);
        this.append(TokenType.Number);
        this.append(TokenType.Semicolon);
        return this;
    }

    @Override
    public String dump() {
        return "    return " + this.subUnits.get(1).dump() + '\n';
    }
}
