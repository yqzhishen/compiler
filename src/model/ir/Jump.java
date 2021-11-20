package model.ir;

public class Jump extends Instruction {

    private final boolean conditional;

    private Operand condition;

    private final int[] labels;

    public Jump(int label) {
        this.conditional = false;
        this.labels = new int[] { label };
    }

    public Jump(Operand condition, int labelIfTrue, int labelIfFalse) {
        this.conditional = true;
        this.condition = condition;
        this.labels = new int[] { labelIfTrue, labelIfFalse };
    }

    public boolean isConditional() {
        return conditional;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Br;
    }

    @Override
    public String toString() {
        if (conditional) {
            return "br i1 " + condition + ", label %" + labels[0] + ", label %" + labels[1];
        }
        else {
            return "br label %" + labels[0];
        }
    }
}
