package model.ir;

public class Return extends Instruction {

    private final String returnType;

    private final Operand returned;

    public Return(String returnType, Operand returned) {
        this.returnType = returnType;
        this.returned = returned;
    }

    public Return() {
        this.returnType = null;
        this.returned = null;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Ret;
    }

    @Override
    public String toString() {
        if (returned == null) {
            return "ret void";
        }
        return "ret " + returnType + " " + returned;
    }
}
