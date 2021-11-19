package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class Cond extends AbstractUnit implements IExpr {

    protected final IExpr[] elements = new IExpr[2];

    protected TokenType operator;

    protected Integer tag;

    public Cond() { }

    public Cond(IExpr leftExpr, TokenType operator, IExpr rightExpr) {
        this.elements[0] = leftExpr;
        this.operator = operator;
        this.elements[1] = rightExpr;
    }

    @Override
    public IExpr build() throws IOException, CompileError {
        return new OrCond().build();
    }

    @Override
    public String dump() {
        return null;
    }

    @Override
    public Integer calculate() {
        return null;
    }

    @Override
    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public Integer getTag() {
        return tag;
    }
}
