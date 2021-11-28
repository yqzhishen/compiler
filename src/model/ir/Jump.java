package model.ir;

public class Jump extends Instruction {

    private final boolean conditional;

    private Operand cond;

    private final Label[] labels;

    public Jump(Label label) {
        this.conditional = false;
        this.labels = new Label[] { label };
    }

    public Jump(Operand cond, Label labelIfTrue, Label labelIfFalse) {
        this.conditional = true;
        this.cond = cond;
        this.labels = new Label[] { labelIfTrue, labelIfFalse };
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Br;
    }

    @Override
    public String toString() {
        if (conditional) {
            return "br i1 " + cond + ", label %" + labels[0].getTag() + ", label %" + labels[1].getTag();
        }
        else {
            return "br label %" + labels[0].getTag();
        }
    }
}
