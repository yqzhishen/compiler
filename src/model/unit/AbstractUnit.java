package model.unit;

import error.CompileError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.Token;
import model.token.TokenType;

import java.io.IOException;

public abstract class AbstractUnit implements IUnit {

    protected final Lexer lexer = Lexer.getLexer();

    @Override
    public boolean isTerminator() {
        return false;
    }

    @Override
    public abstract IUnit build() throws IOException, CompileError;

    @Override
    public abstract String dump();

    protected Token require(TokenType ... types) throws IOException, CompileError {
        Token token = this.lexer.readToken();
        if (token == null)
            throw new SyntaxError(Lexer.getReader().getPos(), types, null);
        boolean match = false;
        for (TokenType type : types) {
            if (type.equals(token.getType())) {
                match = true;
                break;
            }
        }
        if (!match)
            throw new SyntaxError(token.getPos(), types, token.getType());
        return token;
    }

}
