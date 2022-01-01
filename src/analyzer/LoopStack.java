package analyzer;

import model.ir.Label;

import java.util.Stack;

public class LoopStack {

    private record Scope(Label head, Label tail) { }

    private static final LoopStack stack = new LoopStack();

    public static LoopStack getInstance() {
        return stack;
    }

    private LoopStack() { }

    private final Stack<Scope> scopes = new Stack<>();

    public void pushLayer(Label head, Label tail) {
        scopes.push(new Scope(head, tail));
    }

    public void popLayer() {
        this.scopes.pop();
    }

    public Label head() {
        if (scopes.isEmpty())
            return null;
        return scopes.peek().head;
    }

    public Label tail() {
        if (scopes.isEmpty())
            return null;
        return scopes.peek().tail;
    }

}
