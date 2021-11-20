package model.ir;

public class Extend extends Instruction {

    private final String inputType;

    private final Operand operand;

    public Extend(String resultType, Operand output, String inputType, Operand operand) {
        super(resultType, output);
        this.output = output;
        this.inputType = inputType;
        this.operand = operand;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Zext;
    }

    @Override
    public String toString() {
        return output + " = zext " + inputType + " " + operand + " to " + resultType;
    }
}
