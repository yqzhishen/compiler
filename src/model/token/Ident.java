package model.token;

import model.unit.IExpr;

public class Ident extends Token implements IExpr {

    private final String name;

    public Ident(String name) {
        super(TokenType.Ident);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.name + ")";
    }

    @Override
    public Integer calculate() {
        return null;
    }

}
