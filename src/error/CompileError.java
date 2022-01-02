package error;

public class CompileError extends Exception {

    public CompileError() {}

    public CompileError(String message) {
        super(message);
    }

}
