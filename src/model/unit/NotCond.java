package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.symbol.Const;
import model.symbol.Symbol;
import model.symbol.SymbolType;
import model.symbol.Variable;
import model.token.Ident;
import model.token.Number;

import java.util.ArrayList;
import java.util.List;

public class NotCond extends Cond {

    private final IExpr expr;

    public NotCond(IExpr expr) {
        this.expr = expr;
    }

    public IExpr getExpr() {
        return expr;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        if (expr instanceof Cond condition) {
            instructions.addAll(condition.generateIr());
            Operand result = new Operand(true, Tagger.newTag());
            Operate operate = new Operate("i1", result, Operate.OpType.Xor,
                    condition.getResult(),
                    new Operand(false, 1));
            this.result = result;
            instructions.add(operate);
        }
        else if (expr instanceof Number number) {
            Operand result = new Operand(true, Tagger.newTag());
            Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                    new Operand(false, number.getValue()),
                    new Operand(false, 0));
            this.result = result;
            instructions.add(compare);
        }
        else if (expr instanceof Ident ident) {
            Symbol symbol = this.table.get(ident, SymbolType.Variable);
            if (symbol instanceof Const constant) {
                Operand result = new Operand(true, Tagger.newTag());
                Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                        new Operand(false, constant.getValue()),
                        new Operand(false, 0));
                this.result = result;
                instructions.add(compare);
            }
            else if (symbol instanceof Variable variable) {
                Operand tmp = new Operand(true, Tagger.newTag());
                Load load = new Load("i32", tmp, "i32*", variable.getAddress());
                instructions.add(load);
                Operand result = new Operand(true, Tagger.newTag());
                Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                        tmp,
                        new Operand(false, 0));
                this.result = result;
                instructions.add(compare);
            }
        }
        else if (expr instanceof Expr expression) {
            instructions.addAll(expression.dump());
            if (expression instanceof FuncCall call && expression.getResult() == null) {
                throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
            }
            Operand result = new Operand(true, Tagger.newTag());
            Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                    expression.getResult(),
                    new Operand(false, 0));
            this.result = result;
            instructions.add(compare);
        }
        return instructions;
    }

}
