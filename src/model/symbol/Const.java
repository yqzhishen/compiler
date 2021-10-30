package model.symbol;

import model.token.Ident;
import model.unit.IExpr;

public class Const extends Symbol {

    private final IExpr expr;

    public Const(Ident ident, IExpr expr) {
        super(ident);
        this.expr = expr;
    }

    public IExpr getExpr() {
        return expr;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Const;
    }

}
