package model.unit;

import error.CompileError;
import error.LexicalError;
import error.SyntaxError;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;

public class Stmt extends AbstractUnit {

    private int retVal;

    @Override
    public Stmt build() throws IOException, CompileError {
        this.require(TokenType.Return);
        this.retVal = ((Number) this.require(TokenType.Number)).getValue();
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public String dump() {
        return "ret i32 " + this.retVal;
    }
}
