package model.unit;

import analyzer.CondFlow;
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

    @Override
    public boolean isInverted() {
        return expr instanceof Cond;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        if (expr instanceof Cond condition) {
            CondFlow flow = CondFlow.getInstance();
            flow.invert();
            instructions.addAll(condition.generateIr());
            flow.invert();
            this.result = condition.getResult();
        }
        else if (expr instanceof Number number) {
            Operand result = Operand.local(Tagger.newTag());
            Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                    Operand.number(number.getValue()),
                    Operand.number(0));
            this.result = result;
            instructions.add(compare);
        }
        else if (expr instanceof Ident ident) {
            Symbol symbol = this.table.get(ident, SymbolType.Variable);
            if (symbol instanceof Const constant) {
                Operand result = Operand.local(Tagger.newTag());
                Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                        Operand.number(constant.getValue()),
                        Operand.number(0));
                this.result = result;
                instructions.add(compare);
            }
            else if (symbol instanceof Variable variable) {
                Operand tmp = Operand.local(Tagger.newTag());
                Load load = new Load("i32", tmp, "i32*", variable.getAddress());
                instructions.add(load);
                Operand result = Operand.local(Tagger.newTag());
                Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                        tmp,
                        Operand.number(0));
                this.result = result;
                instructions.add(compare);
            }
        }
        else if (expr instanceof Expr expression) {
            instructions.addAll(expression.generateIr());
            if (expression instanceof FuncCall call && expression.getResult() == null) {
                throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
            }
            Operand result = Operand.local(Tagger.newTag());
            Compare compare = new Compare(result, Compare.CmpType.Eq, "i32",
                    expression.getResult(),
                    Operand.number(0));
            this.result = result;
            instructions.add(compare);
        }
        return instructions;
    }

}
