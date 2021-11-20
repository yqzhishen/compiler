package model.ir;

public class Label extends Instruction {

    private final int tag;

    public Label(int tag) {
        this.tag = tag;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Label;
    }

    @Override
    public String toString() {
        return "\n" + tag + ":";
    }
}
