package lexer.automaton;

import model.token.TokenType;

public class SimpleState implements IState {

    private char condition;

    private int transfer;

    private final TokenType type;

    public SimpleState(TokenType type, char condition, int transfer) {
        this.condition = condition;
        this.transfer = transfer;
        this.type = type;
    }

    public SimpleState(TokenType type) {
        this.type = type;
    }

    @Override
    public int recognize(char ch) {
        if (ch == this.condition)
            return this.transfer;
        return -1;
    }

    @Override
    public TokenType terminatorType() {
        return this.type;
    }
}
