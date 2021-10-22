package model.unit;

import error.CompileError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.Token;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUnit implements IUnit {

    protected final List<IUnit> subUnits = new ArrayList<>();

    protected final Lexer lexer = Lexer.getLexer();

    @Override
    public boolean isTerminator() {
        return false;
    }

    @Override
    public List<IUnit> subUnits() {
        return this.subUnits;
    }

    @Override
    public abstract IUnit build() throws IOException, CompileError;

    @Override
    public String dump() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (; i < this.subUnits.size() - 1; ++i) {
            builder.append(this.subUnits.get(i).dump());
            builder.append(' ');
        }
        if (i < this.subUnits.size())
            builder.append(this.subUnits.get(i).dump());
        return builder.toString();
    }

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
        this.subUnits.add(token);
        return token;
    }

}
