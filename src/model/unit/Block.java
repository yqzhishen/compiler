package model.unit;

import error.LexicalError;
import error.SyntaxError;
import model.token.TokenType;

import java.io.IOException;

public class Block extends AbstractUnit {

    @Override
    public IUnit build() throws LexicalError, SyntaxError, IOException {
        this.append(TokenType.LBrace);
        this.subUnits.add(new Stmt().build());
        this.append(TokenType.RBrace);
        return this;
    }

    @Override
    public String dump() {
        return "{\n" + this.subUnits.get(1).dump() + "}";
    }

}
