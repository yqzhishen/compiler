package model.ir;

public class Label extends Instruction {

    private int tag;

    public Label(int tag) {
        this.tag = tag;
    }

    public Label() { }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return this.tag;
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
