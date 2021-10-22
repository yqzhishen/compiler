package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class Expr extends AbstractUnit implements IExpr {

    protected final IExpr[] elements = new IExpr[2];

    protected TokenType operator;

    public Expr() { }

    public Expr(IExpr leftExpr, TokenType operator, IExpr rightExpr) {
        this.elements[0] = leftExpr;
        this.operator = operator;
        this.elements[1] = rightExpr;
    }

    @Override
    public IExpr build() throws IOException, CompileError {
        return new AddExpr().build();
    }

    @Override
    public String dump() {
        return String.valueOf(this.calculate());
    }

    @Override
    public int calculate() {
        int[] values = new int[] {
                this.elements[0].calculate(),
                this.elements[1].calculate()
        };
        return switch (this.operator) {
            case Plus -> values[0] + values[1];
            case Sub -> values[0] - values[1];
            case Mul -> values[0] * values[1];
            case Div -> values[0] / values[1];
            case Mod -> values[0] % values[1];
            default -> 0;
        };
    }

}
