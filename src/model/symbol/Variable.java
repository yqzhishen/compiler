package model.symbol;

import model.token.Ident;
import model.unit.IExpr;

public class Variable extends Symbol {

    private final IExpr expr;

    public Variable(Ident ident, IExpr expr) {
        super(ident);
        this.expr = expr;
    }

    public IExpr getExpr() {
        return expr;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Variable;
    }

}
