package model.unit;

import error.CompileError;
import error.LexicalError;
import error.SyntaxError;
import model.token.TokenType;

import java.io.IOException;

public class Stmt extends AbstractUnit {

    @Override
    public IUnit build() throws IOException, CompileError {
        this.require(TokenType.Return);
        this.require(TokenType.Number);
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public String dump() {
        return "    ret " + this.subUnits.get(1).dump() + '\n';
    }
}
