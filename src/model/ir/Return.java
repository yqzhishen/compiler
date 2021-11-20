package model.ir;

public class Return extends Instruction {

    private final String returnType;

    private final Operand returned;

    public Return(String returnType, Operand returned) {
        this.returnType = returnType;
        this.returned = returned;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Ret;
    }

    @Override
    public String toString() {
        return "ret " + returnType + " " + returned;
    }
}
