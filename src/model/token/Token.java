package model.token;

import model.unit.IUnit;

import java.util.List;

public class Token implements IUnit {

    protected final TokenType type;

    public Token(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type.name();
    }

    public TokenType getType() {
        return type;
    }

    protected Token filterIndent(String param) {
        if (this.type.equals(TokenType.Ident)) {
            return switch (param) {
                case "int" -> new Token(TokenType.Int);
                case "main" -> new Token(TokenType.Main);
                case "return" -> new Token(TokenType.Return);
                default -> new Ident(param);
            };
        }
        return this;
    }

    protected Token filterNumber(String param) {
        return switch (this.type) {
            case Dec -> new Number(Integer.parseInt(param));
            case Oct -> new Number(Integer.parseInt(param, 8));
            case Hex -> new Number(Integer.parseInt(param.substring(2), 16));
            default -> this;
        };
    }

    public Token filter(String param) {
        return this
                .filterIndent(param)
                .filterNumber(param);
    }

    @Override
    public boolean isTerminator() {
        return true;
    }

    @Override
    public List<IUnit> subUnits() {
        return null;
    }

    @Override
    public String dump() {
        return this.type.dump();
    }
}
