package model.token;

public class Number extends Token {

    int value;

    public Number(int value) {
        super(TokenType.Number);
        this.value = value;
    }

    int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.value + ")";
    }

}
