package reader;

public class FilePosition {

    int line = 1;

    int column = 0;

    public FilePosition() { }

    public FilePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("line %d, column %d", this.line, this.column);
    }

    public FilePosition copy() {
        return new FilePosition(this.line, this.column);
    }

}
