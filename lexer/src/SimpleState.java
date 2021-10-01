public class SimpleState implements IState {

    private char condition;

    private int transfer;

    private final TokenType type;

    public SimpleState(char condition, int transfer, TokenType type) {
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
    public TokenType endTokenType() {
        return this.type;
    }
}
