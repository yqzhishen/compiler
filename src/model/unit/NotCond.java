package model.unit;

public class NotCond extends Cond {

    private final IExpr expr;

    public NotCond(IExpr expr) {
        this.expr = expr;
    }

    public IExpr getExpr() {
        return expr;
    }

}
