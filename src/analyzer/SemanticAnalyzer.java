package analyzer;

import error.CompileError;
import model.unit.CompUnit;

public class SemanticAnalyzer {

    private static final SemanticAnalyzer analyzer = new SemanticAnalyzer();

    public static SemanticAnalyzer getAnalyzer() {
        return analyzer;
    }

    private SemanticAnalyzer() { }

    public String dump(CompUnit unit) throws CompileError {
        return unit.generateCode();
    }

}
