package model.token;

public class Ident extends Token {

    private final String name;

    public Ident(String name) {
        super(TokenType.Ident);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.name + ")";
    }

}
