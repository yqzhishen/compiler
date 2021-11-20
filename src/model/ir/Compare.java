package model.ir;

public class Compare extends Instruction {

    public enum CmpType {

        Eq("eq"),
        Neq("ne"),
        Lt("slt"),
        Lte("sle"),
        Gt("sgt"),
        Gte("sge");

        private final String name;

        CmpType(String name) {
            this.name = name;
        }

    }

    private final CmpType comparator;

    private final String inputType;

    private final Operand[] operands = new Operand[2];

    public Compare(Operand output, CmpType comparator, String inputType, Operand op1, Operand op2) {
        this.output = output;
        this.comparator = comparator;
        this.inputType = inputType;
        this.operands[0] = op1;
        this.operands[1] = op2;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Icmp;
    }

    @Override
    public String toString() {
        return output + " = icmp " + comparator.name + " " + inputType + " " + operands[0] + ", " + operands[1];
    }
}
