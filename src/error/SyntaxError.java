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
        StringBuilder builder = new StringBuilder("Syntax error at ").append(this.pos).append(": expected ");
        StringJoiner exp = new StringJoiner("> | <", "<", ">");
        for (TokenType type : this.expected) {
            exp.add(type.name());
        }
        builder.append(exp);
        if (this.got != null) {
            builder.append(", got <").append(got.name()).append("> instead");
        }
        return builder.toString();
    }

}
