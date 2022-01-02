package model.symbol;

import model.ir.Operand;
import model.token.Ident;
import model.token.Number;
import model.unit.ArrayInitializer;
import model.unit.IExpr;

import java.util.List;

public class Array extends Symbol {

    public static Array constArray(Ident ident, List<IExpr> shape, ArrayInitializer initializer) {
        return new Array(ident, true, shape, initializer);
    }

    public static Array varArray(Ident ident, List<IExpr> shape, ArrayInitializer initializer) {
        return new Array(ident, false, shape, initializer);
    }

    private final boolean isConst;

    private final List<IExpr> shape;

    private final ArrayInitializer initializer;

    private Operand address;

    private Array(Ident ident, boolean isConst, List<IExpr> shape, ArrayInitializer initializer) {
        super(ident);
        this.isConst = isConst;
        this.shape = shape;
        this.initializer = initializer;
    }

    public boolean isConst() {
        return isConst;
    }

    public List<IExpr> getShape() {
        return shape;
    }

    public String getShapeToString() {
        return dumpShape(this.shape);
    }

    public static String dumpShape(List<IExpr> shape) {
        StringBuilder builder = new StringBuilder();
        if (!shape.isEmpty() && ((Number) shape.get(0)).getValue() == -1) {
            shape = shape.subList(1, shape.size());
        }
        for (IExpr expr : shape) {
            Number number = (Number) expr;
            builder.append('[').append(number.getValue()).append(" x ");
        }
        builder.append("i32");
        builder.append("]".repeat(shape.size()));
        return builder.toString();
    }

    public ArrayInitializer getInitializer() {
        return initializer;
    }

    public void setAddress(Operand address) {
        this.address = address;
    }

    public Operand getAddress() {
        return address;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Array;
    }

}
