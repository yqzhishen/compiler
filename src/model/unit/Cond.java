package model.unit;

import analyzer.CondScope;
import analyzer.SymTable;
import analyzer.Tagger;
import error.CompileError;
import model.ir.*;
import model.ir.Operate.OpType;
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
        Operand[] operands = new Operand[2];
        Cond subCond = (Cond) elements[0];
        Label next = new Label();
        if (operator.equals(TokenType.And)) {
            scope.pushPass(next, scope.pass(false));
        }
        else {
            scope.pushPass(scope.pass(true), next);
        }
        List<Instruction> instructions = new ArrayList<>(subCond.generateIr());
        operands[0] = subCond.getResult();
        scope.popPass();
        next.setTag(Tagger.newTag());
        Jump jump;
        if (operator.equals(TokenType.And)) {
            jump = new Jump(operands[0], next, scope.pass(false));
        }
        else {
            jump = new Jump(operands[0], scope.pass(true), next);
        }
        instructions.add(jump);
        instructions.add(next);
        subCond = (Cond) elements[1];
        instructions.addAll(subCond.generateIr());
        operands[1] = subCond.getResult();
        OpType op = switch (operator) {
            case And -> OpType.And;
            case Or -> OpType.Or;
            default -> null; // This shall never happen
        };
        Operand result = Operand.local(Tagger.newTag());
        Operate operate = new Operate("i1", result, op, operands[0], operands[1]);
        this.result = result;
        instructions.add(operate);
        return instructions;
    }

    @Override
    public Integer calculate() {
        throw new UnsupportedOperationException("Calculation of condition is not supported");
    }

}
