package error;

import model.token.TokenType;
import reader.FilePosition;

import java.util.StringJoiner;

public class SyntaxError extends CompileError {

    private final FilePosition pos;

    private final TokenType[] expected;

    private final TokenType got;

    public SyntaxError(FilePosition pos, TokenType[] expected, TokenType got) {
        this.pos = pos;
        this.expected = expected;
        this.got = got;
    }

    @Override
    public String getMessage() {
        if (this.got == null)
            return "Syntax error at " + this.pos + ": unexpected end of file";
        StringJoiner exp = new StringJoiner("> | <", "<", ">");
        for (TokenType type : this.expected) {
            exp.add(type.name());
        }
        return "Syntax error at " + this.pos + ": expected " + exp + ", got <" + got.name() + "> instead";
    }

}
