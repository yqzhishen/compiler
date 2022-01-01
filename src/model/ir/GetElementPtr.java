package model.ir;

import java.util.List;

public class GetElementPtr extends Instruction {

    private final String inputType;

    private final Operand input;

    private final List<Operand> indexes;

    public GetElementPtr(Operand output, String inputType, Operand input, List<Operand> indexes) {
        this.output = output;
        this.inputType = inputType;
        this.input = input;
        this.indexes = indexes;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.GEP;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append(output)
                .append(" = getelementptr ")
                .append(inputType)
                .append(", ")
                .append(inputType)
                .append("* ")
                .append(input);
        for (Operand operand : indexes) {
            builder.append(", i32 ").append(operand);
        }
        return builder.toString();
    }
}
