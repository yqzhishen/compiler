package reader;

import java.util.Stack;

public class FilePosition {

    private int line = 1;

    private int column = 0;

    // TODO: 2021/10/31 Move this to CompileReader
    private final Stack<Integer> stack = new Stack<>();

    public FilePosition() { }

    public FilePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    protected void nextLine() {
        ++this.line;
        this.stack.push(this.column);
        this.column = 0;
    }

    protected void nextColumn() {
        ++this.column;
    }

    protected void prevLine() {
        --this.line;
        this.column = this.stack.pop();
    }

    protected void prevColumn() {
        --this.column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    @Override
    public String toString() {
        return String.format("line %d, column %d", this.line, this.column);
    }

    public FilePosition copy() {
        return new FilePosition(this.line, this.column);
    }

}
