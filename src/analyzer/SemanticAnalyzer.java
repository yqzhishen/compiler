package analyzer;

import error.CompileError;
import model.ir.Instruction;
import model.unit.CompUnit;

import java.util.List;
import java.util.StringJoiner;

public class SemanticAnalyzer {

    private static final SemanticAnalyzer analyzer = new SemanticAnalyzer();

    public static SemanticAnalyzer getAnalyzer() {
        return analyzer;
    }

    private SemanticAnalyzer() { }

    public String dump(CompUnit unit) throws CompileError {
        StringBuilder builder = new StringBuilder("define dso_local i32 @main() {");
        StringJoiner joiner = new StringJoiner("\n    ", "\n    ", "\n");
        List<Instruction> instructions = unit
                .getFuncDefs().get(0)
                .getFuncBlock()
                .generateIr();
        instructions.forEach(instruction -> joiner.add(instruction.toString()));
        builder.append(joiner).append("}\n");
        return builder.toString();
    }

}
