package model.unit;

import analyzer.CondFlow;
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

    protected boolean inverted;

    protected SymTable table = SymTable.getInstance();

    public boolean isInverted() {
        return inverted;
    }

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
        CondFlow flow = CondFlow.getInstance();
        Cond subCond = (Cond) elements[0];
        Label next = new Label();
        if (operator.equals(TokenType.And)) {
            flow.push(next, flow.pass(false));
        }
        else {
            flow.push(flow.pass(true), next);
        }
        List<Instruction> instructions = new ArrayList<>(subCond.generateIr());
        next.setTag(Tagger.newTag());
        Jump jump = new Jump(subCond.getResult(), flow.pass(!subCond.isInverted()), flow.pass(subCond.isInverted()));
        flow.pop();
        instructions.add(jump);
        instructions.add(next);
        subCond = (Cond) elements[1];
        instructions.addAll(subCond.generateIr());
        this.inverted = subCond.isInverted();
        this.result = subCond.getResult();
        return instructions;
    }

    @Override
    public Integer calculate() {
        throw new UnsupportedOperationException("Calculation of condition is not supported");
    }

}
