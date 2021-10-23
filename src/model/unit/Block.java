package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class Block extends AbstractUnit {

    private Stmt stmt;

    public Stmt getStmt() {
        return this.stmt;
    }

    @Override
    public Block build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        this.stmt = new Stmt().build();
        this.require(TokenType.RBrace);
        return this;
    }

    @Override
    public String dump() {
        return "{\n    " + this.stmt.dump() + "\n}";
    }

}
