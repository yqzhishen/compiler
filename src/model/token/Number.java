package model.token;

import model.unit.IExpr;

public class Number extends Token implements IExpr {

    private int value;

    public Number(int value) {
        super(TokenType.Number);
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.value + ")";
    }

    @Override
    public Integer calculate() {
        return this.value;
    }

}
