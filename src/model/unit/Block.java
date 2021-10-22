package model.unit;

import error.CompileError;
import error.LexicalError;
import error.SyntaxError;
import model.token.TokenType;

import java.io.IOException;

public class Block extends AbstractUnit {

    @Override
    public IUnit build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        this.subUnits.add(new Stmt().build());
        this.require(TokenType.RBrace);
        return this;
    }

    @Override
    public String dump() {
        return "{\n" + this.subUnits.get(1).dump() + "}";
    }

}
