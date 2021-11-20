package model.ir;

import java.util.List;
import java.util.StringJoiner;

public class Call extends Instruction {

    public record Param(String paramType, Operand operand) { }

    private final String functionName;

    private final List<Param> params;

    public Call(String functionName, List<Param> params) {
        this.resultType = "void";
        this.functionName = functionName;
        this.params = params;
    }

    public Call(String resultType, Operand output, String functionName, List<Param> params) {
        super(resultType, output);
        this.functionName = functionName;
        this.params = params;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Call;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.output != null)
            builder.append(output).append(" = ");
        builder.append("call ").append(resultType).append(" @").append(functionName);
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (Param param : params) {
            joiner.add(param.paramType + " " + param.operand);
        }
        builder.append(joiner);
        return builder.toString();
    }
}
