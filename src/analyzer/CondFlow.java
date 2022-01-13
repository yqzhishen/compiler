package analyzer;

import model.ir.Label;

import java.util.Stack;

public class CondFlow {

    private record Pair(Label labelTrue, Label labelFalse) { }

    private static final CondFlow flow = new CondFlow();

    public static CondFlow getInstance() {
        return flow;
    }

    private CondFlow() { }

    private final Stack<Pair> labels = new Stack<>();

    public void push(Label labelTrue, Label labelFalse) {
        labels.push(new Pair(labelTrue, labelFalse));
    }

    public void pop() {
        labels.pop();
    }

    public Label pass(boolean result) {
        return result ? labels.peek().labelTrue : labels.peek().labelFalse;
    }

    public void invert() {
        Pair peek = labels.pop();
        labels.push(new Pair(peek.labelFalse, peek.labelTrue));
    }

}
