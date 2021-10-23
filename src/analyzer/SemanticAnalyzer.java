package analyzer;

import model.token.Number;
import model.token.TokenType;
import model.unit.CompUnit;
import model.unit.Expr;
import model.unit.IExpr;

import java.util.StringJoiner;

public class SemanticAnalyzer {

    private static final SemanticAnalyzer analyzer = new SemanticAnalyzer();

    public static SemanticAnalyzer getAnalyzer() {
        return analyzer;
    }

    private int count = 0;

    private final StringJoiner joiner = new StringJoiner("\n    ", "\n    ", "\n");

    private SemanticAnalyzer() { }

    public String dump(CompUnit unit) {
        StringBuilder builder = new StringBuilder("define dso_local i32 @main() {");
        IExpr expr = unit
                .getFuncDefs().get(0)
                .getFuncBlock()
                .getStmt()
                .getExpr();
        if (expr instanceof Number number) {
            this.joiner.add("ret i32 " + number.getValue());
        }
        else if (expr instanceof Expr expression) {
            dumpExpr(expression);
            this.joiner.add("ret i32 %var" + expression.getCount());
        }
        builder.append(this.joiner);
        builder.append("}\n");
        return builder.toString();
    }

    public void dumpExpr(Expr expr) {
        IExpr[] elements = expr.getElements();
        if (elements[0] instanceof Number number1 && elements[1] instanceof Number number2) {
            this.joiner.add("%var" + this.count + " = " + dumpOp(expr.getOperator()) + " i32 " + number1.getValue() + ", " + number2.getValue());
            expr.setCount(this.count++);
            return;
        }
        if (elements[0] instanceof Expr expr1) {
            dumpExpr(expr1);
        }
        if (elements[1] instanceof Expr expr2) {
            dumpExpr(expr2);
        }
        String e1 = elements[0] instanceof Number number ? String.valueOf(number.getValue()) : "%var" + ((Expr) elements[0]).getCount();
        String e2 = elements[1] instanceof Number number ? String.valueOf(number.getValue()) : "%var" + ((Expr) elements[1]).getCount();
        this.joiner.add(String.format("%%var%d = %s i32 %s, %s", this.count, dumpOp(expr.getOperator()), e1, e2));
        expr.setCount(this.count++);
    }

    private String dumpOp(TokenType op) {
        return switch (op) {
            case Plus -> "add";
            case Sub -> "sub";
            case Mul -> "mul";
            case Div -> "sdiv";
            case Mod -> "rems";
            default -> null;
        };
    }

}
