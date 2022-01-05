package model.unit;

import error.CompileError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.Token;
import model.token.TokenType;

public abstract class AbstractUnit implements IUnit {

    protected final Lexer lexer = Lexer.getLexer();

    @Override
    public abstract IUnit build() throws CompileError;

    protected Token require(TokenType ... types) throws CompileError {
        Token token = this.lexer.getToken();
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
