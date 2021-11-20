package model.symbol;

import model.ir.Operand;
import model.token.Ident;
import model.unit.IExpr;

public class Variable extends Symbol {

    private final IExpr expr;

    private Operand address;

    public Variable(Ident ident, IExpr expr) {
        super(ident);
        this.expr = expr;
    }

    public IExpr getExpr() {
        return expr;
    }

    public void setAddress(Operand address) {
        this.address = address;
    }

    public Operand getAddress() {
        return address;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Variable;
    }

}
