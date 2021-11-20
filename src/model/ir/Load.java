package model.ir;

public class Load extends Instruction {

    private final String addressType;

    private final Operand address;

    public Load(String resultType, Operand output, String addressType, Operand address) {
        super(resultType, output);
        this.addressType = addressType;
        this.address = address;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Load;
    }

    @Override
    public String toString() {
        return output + " = load " + resultType + ", " + addressType + " " + address;
    }
}
