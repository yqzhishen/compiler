package model.ir;

public class Operate extends Instruction {

    public enum OpType {

        Add("add"),
        Sub("sub"),
        Mul("mul"),
        Div("sdiv"),
        Mod("srem");

        private final String name;

        OpType(String name) {
            this.name = name;
        }

    }

    private final OpType operator;

    private final Operand[] operands = new Operand[2];

    public Operate(String resultType, Operand output, OpType operator, Operand op1, Operand op2) {
        super(resultType, output);
        this.operator = operator;
        this.operands[0] = op1;
        this.operands[1] = op2;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Op;
    }

    @Override
    public String toString() {
        return output + " = " + operator.name + " " + resultType + " " + operands[0] + ", " + operands[1];
    }
}
