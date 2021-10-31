package model.symbol;

import model.token.Ident;
import model.unit.IExpr;

public class Const extends Symbol {

    private IExpr expr;

    private int value;

    public Const(Ident ident, IExpr expr) {
        super(ident);
        this.expr = expr;
    }

    public Const(Ident ident, int value) {
        super(ident);
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public IExpr getExpr() {
        return expr;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Const;
    }

}
