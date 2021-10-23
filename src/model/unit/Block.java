package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;

public class Block extends AbstractUnit {

    private final ArrayList<Stmt> stmt = new ArrayList<>();

    public ArrayList<Stmt> getStatements() {
        return this.stmt;
    }

    @Override
    public Block build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        this.stmt.add(new Stmt().build());
        this.require(TokenType.RBrace);
        return this;
    }

    @Override
    public String dump() {
        return "{\n    " + this.stmt.get(0).dump() + "\n}";
    }

}
