package error;

import reader.FilePosition;

public class SemanticError extends CompileError {

    private FilePosition pos;

    private String message;

    public SemanticError() { }

    public SemanticError(FilePosition pos, String message) {
        this.message = message;
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        return "Semantic error at " + this.pos + ": " + this.message;
    }

}
