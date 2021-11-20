package model.ir;

public class Allocate extends Instruction {

    public Allocate(String resultType, Operand output) {
        super(resultType, output);
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Alloca;
    }

    @Override
    public String toString() {
        return output + " = alloca " + resultType;
    }
}
