package model.ir;

public class Extend extends Instruction {

    private final String inputType;

    private final Operand input;

    public Extend(String resultType, Operand output, String inputType, Operand input) {
        super(resultType, output);
        this.inputType = inputType;
        this.input = input;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Zext;
    }

    @Override
    public String toString() {
        return output + " = zext " + inputType + " " + input + " to " + resultType;
    }
}
