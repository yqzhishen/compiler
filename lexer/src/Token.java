public class Token {

    private final TokenType type;

    private String param;

    public Token(TokenType type) {
        this.type = type;
    }

    public Token(TokenType type, String param) {
        this.type = type;
        this.param = param;
    }

    @Override
    public String toString() {
        if (this.param == null) {
            return type.name();
        }
        else {
            return type.name() + "(" + this.param + ")";
        }
    }

    public TokenType getType() {
        return type;
    }

    public String getParam() {
        return param;
    }

}
