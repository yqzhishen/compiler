package model.token;

import model.unit.IUnit;
import reader.FilePosition;

public class Token implements IUnit {

    protected final TokenType type;

    protected FilePosition pos;

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

    public Token setPos(FilePosition pos) {
        this.pos = pos;
        return this;
    }

    public FilePosition getPos() {
        return this.pos;
    }

    protected Token filterIndent(String param) {
        if (this.type.equals(TokenType.Ident)) {
            return switch (param) {
                case "const" -> new Token(TokenType.Const);
                case "int" -> new Token(TokenType.Int);
                case "main" -> new Token(TokenType.Main);
                case "if" -> new Token(TokenType.If);
                case "else" -> new Token(TokenType.Else);
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

}
