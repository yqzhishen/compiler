package model.unit;

import analyzer.CondScope;
import analyzer.SymTable;
import analyzer.Tagger;
import error.CompileError;
import model.ir.Instruction;
import model.ir.Jump;
import model.ir.Label;
import model.ir.Operand;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Cond extends AbstractUnit implements IExpr {

    protected final IExpr[] elements = new IExpr[2];

    protected TokenType operator;

    protected Operand result;

    protected SymTable table = SymTable.getInstance();

    public Operand getResult() {
        return result;
    }

    public Cond() { }

    public Cond(IExpr leftExpr, TokenType operator, IExpr rightExpr) {
        this.elements[0] = leftExpr;
        this.operator = operator;
        this.elements[1] = rightExpr;
    }

    @Override
    public IExpr build() throws CompileError {
        return new OrCond().build();
    }

    public List<Instruction> generateIr() throws CompileError {
        CondScope scope = CondScope.getInstance();
        Cond subCond = (Cond) elements[0];
        Label next = new Label();
        if (operator.equals(TokenType.And)) {
            scope.pushPass(next, scope.pass(false));
        }
        else {
            scope.pushPass(scope.pass(true), next);
        }
        List<Instruction> instructions = new ArrayList<>(subCond.generateIr());
        Operand result = subCond.getResult();
        scope.popPass();
        next.setTag(Tagger.newTag());
        Jump jump;
        if (operator.equals(TokenType.And)) {
            jump = new Jump(result, next, scope.pass(false));
        }
        else {
            jump = new Jump(result, scope.pass(true), next);
        }
        instructions.add(jump);
        instructions.add(next);
        subCond = (Cond) elements[1];
        instructions.addAll(subCond.generateIr());
        result = subCond.getResult();
        this.result = result;
        return instructions;
    }

    @Override
    public Integer calculate() {
        throw new UnsupportedOperationException("Calculation of condition is not supported");
    }

}
