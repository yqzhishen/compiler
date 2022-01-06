package analyzer;

import model.ir.Label;

import java.util.Stack;

public class CondScope {

    private record Scope(Label labelTrue, Label labelFalse) { }

    private static final CondScope scope = new CondScope();

    public static CondScope getInstance() {
        return scope;
    }

    private CondScope() { }

    private final Stack<Scope> labels = new Stack<>();

    public void pushPass(Label labelTrue, Label labelFalse) {
        labels.push(new Scope(labelTrue, labelFalse));
    }

    public void popPass() {
        labels.pop();
    }

    public Label pass(boolean result) {
        return result ? labels.peek().labelTrue : labels.peek().labelFalse;
    }

}
