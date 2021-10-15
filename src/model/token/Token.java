package model.token;

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

    private Token filterIndent() {
        if (this.type.equals(TokenType.Ident)) {
            return switch (this.param) {
                case "int" -> new Token(TokenType.Int);
                case "main" -> new Token(TokenType.Main);
                case "return" -> new Token(TokenType.Return);
                default -> this;
            };
        }
        return this;
    }

    private Token filterNumber() {
        return switch (this.type) {
            case Dec -> new Token(TokenType.Number, this.param);
            case Oct -> new Token(TokenType.Number, String.valueOf(Integer.parseInt(this.param, 8)));
            case Hex -> new Token(TokenType.Number, String.valueOf(Integer.parseInt(this.param.substring(2), 16)));
            default -> this;
        };
    }

    private Token filterParam() {
        if (!this.type.equals(TokenType.Ident) && !this.type.equals(TokenType.Number))
            this.param = null;
        return this;
    }

    public Token filter() {
        return this
                .filterIndent()
                .filterNumber()
                .filterParam();
    }

}
