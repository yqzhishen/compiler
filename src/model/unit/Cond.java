package model.unit;

import analyzer.SymTable;
import analyzer.Tagger;
import error.CompileError;
import model.ir.Instruction;
import model.ir.Operand;
import model.ir.Operate;
import model.ir.Operate.OpType;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cond extends AbstractUnit implements IExpr {

    protected final IExpr[] elements = new IExpr[2];

    protected TokenType operator;

    protected Operand result;

    protected SymTable table = SymTable.getSymTable();

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
    public IExpr build() throws IOException, CompileError {
        return new OrCond().build();
    }

    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Operand[] operands = new Operand[2];
        for (int i = 0; i < 2; ++i) {
            Cond subCond = (Cond) elements[i];
            instructions.addAll(subCond.generateIr());
            operands[i] = subCond.getResult();
        }
        OpType op = switch (operator) {
            case And -> OpType.And;
            case Or -> OpType.Or;
            default -> null;
        };
        Operand result = new Operand(true, Tagger.newTag());
        Operate operate = new Operate("i1", result, op, operands[0], operands[1]);
        this.result = result;
        instructions.add(operate);
        return instructions;
    }

    @Override
    public Integer calculate() {
        return null;
    }

}
