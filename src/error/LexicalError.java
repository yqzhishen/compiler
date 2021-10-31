package error;

import reader.FilePosition;

public class LexicalError extends CompileError {

    private FilePosition pos;

    private int got;

    public LexicalError() { }

    public LexicalError(FilePosition pos, int got) {
        this.setParam(pos, got);
    }

    public void setParam(FilePosition pos, int got) {
        this.pos = pos;
        this.got = got;
    }

    @Override
    public String getMessage() {
        if (this.got == -1) {
            return "Lexical error at " + this.pos + ": unexpected end of file";
        }
        return "Lexical error at " + this.pos + ": illegal character '" + (char) this.got + "'";
    }

}
