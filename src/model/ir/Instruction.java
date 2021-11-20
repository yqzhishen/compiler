package model.ir;

public abstract class Instruction {

    protected String resultType;

    protected Operand output;

    public Instruction() { }

    public Instruction(String resultType, Operand output) {
        this.resultType = resultType;
        this.output = output;
    }

    public abstract InstructionType getType();

    @Override
    public abstract String toString();

}
