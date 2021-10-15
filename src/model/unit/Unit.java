package model.unit;

import model.token.Token;

import java.util.ArrayList;

public class Unit {

    private final boolean isTerminator;

    private final Token token;

    private final ArrayList<Unit> subUnits;

    private Unit(Token token) {
        this.isTerminator = true;
        this.token = token;
        this.subUnits = null;
    }

    private Unit() {
        this.isTerminator = false;
        this.token = null;
        this.subUnits = new ArrayList<>();
    }

    public static Unit terminator(Token token) {
        return new Unit(token);
    }

    public static Unit nonTerminator() {
        return new Unit();
    }

}
