package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class Stmt extends AbstractUnit {

    private IExpr expr;

    @Override
    public Stmt build() throws IOException, CompileError {
        this.require(TokenType.Return);
        this.expr = new Expr().build();
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public String dump() {
        return "ret i32 " + this.expr.dump();
    }
}
