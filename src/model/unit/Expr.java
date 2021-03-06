package model.unit;

import analyzer.SymTable;
import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.ir.Operate.OpType;
import model.symbol.*;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Expr extends AbstractUnit implements IExpr {

    protected final IExpr[] elements = new IExpr[2];

    protected TokenType operator;

    protected Operand result;

    protected boolean bool;

    protected SymTable table = SymTable.getInstance();

    public Expr() { }

    public Expr(boolean bool) {
        this.bool = bool;
    }

    public Expr(IExpr leftExpr, TokenType operator, IExpr rightExpr) {
        this.elements[0] = leftExpr;
        this.operator = operator;
        this.elements[1] = rightExpr;
    }

    public Operand getResult() {
        return result;
    }

    @Override
    public IExpr build() throws CompileError {
        return new AddExpr(bool).build();
    }

    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Operand[] operands = new Operand[2];
        for (int i = 0; i < 2; ++i) {
            IExpr subExpr = elements[i];
            if (subExpr instanceof Number number) {
                operands[i] = Operand.number(number.getValue());
            }
            else if (subExpr instanceof Ident ident) {
                Symbol symbol = this.table.get(ident, SymbolType.Variable);
                if (symbol instanceof Const constant) {
                    operands[i] = Operand.number(constant.getValue());
                }
                else if (symbol instanceof Variable variable) {
                    operands[i] = Operand.local(Tagger.newTag());
                    Load load = new Load("i32", operands[i], "i32*", variable.getAddress());
                    instructions.add(load);
                }
                else if (symbol instanceof Array array) {
                    throw new SemanticError(ident.getPos(), "expected index for array '" + array.getIdent().getName() + '\'');
                }
            }
            else if (subExpr instanceof Expr expression) {
                instructions.addAll(expression.generateIr());
                if (expression instanceof FuncCall call && expression.getResult() == null) {
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                }
                operands[i] = expression.getResult();
            }
            else if (subExpr instanceof Cond condition) {
                instructions.addAll(condition.generateIr());
                Operand extended = Operand.local(Tagger.newTag());
                Extend extend = new Extend("i32", extended, "i1", condition.getResult());
                instructions.add(extend);
                operands[i] = extended;
            }
        }
        OpType op = switch (operator) {
            case Add -> OpType.Add;
            case Sub -> OpType.Sub;
            case Mul -> OpType.Mul;
            case Div -> OpType.Div;
            case Mod -> OpType.Mod;
            default -> null;
        };
        Operand result = Operand.local(Tagger.newTag());
        Operate operate = new Operate("i32", result, op, operands[0], operands[1]);
        this.result = result;
        instructions.add(operate);
        return instructions;
    }

    @Override
    public Integer calculate() throws CompileError {
        int[] values = new int[2];
        for (int i = 0; i < 2; ++i) {
            IExpr subExpr = elements[i];
            if (subExpr instanceof Number number) {
                values[i] = number.getValue();
            }
            else if (subExpr instanceof Ident ident) {
                Const constant = (Const) table.get(ident, SymbolType.Const);
                values[i] = constant.getValue();
            }
            else if (subExpr instanceof Expr expression) {
                values[i] = expression.calculate();
            }
        }
        return switch (operator) {
            case Add -> values[0] + values[1];
            case Sub -> values[0] - values[1];
            case Mul -> values[0] * values[1];
            case Div -> values[0] / values[1];
            case Mod -> values[0] % values[1];
            default -> null; // This shall never happen
        };
    }

}
