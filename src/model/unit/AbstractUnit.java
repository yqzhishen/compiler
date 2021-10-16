package model.unit;

import error.LexicalError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.Token;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUnit implements IUnit {

    protected final List<IUnit> subUnits = new ArrayList<>();

    protected final Lexer lexer;

    public AbstractUnit(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public boolean isTerminator() {
        return false;
    }

    @Override
    public List<IUnit> subUnits() {
        return this.subUnits;
    }

    @Override
    public abstract IUnit build() throws LexicalError, SyntaxError, IOException;

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

    protected void append(TokenType type) throws LexicalError, IOException, SyntaxError {
        Token token = this.lexer.readToken();
        if (token == null || !token.getType().equals(type))
            throw new SyntaxError();
        this.subUnits.add(token);
    }

}
