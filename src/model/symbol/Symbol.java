package model.symbol;

import model.token.Ident;

public abstract class Symbol {

    protected Ident ident;

    public abstract SymbolType getType();

    public Symbol(Ident ident) {
        this.ident = ident;
    }

    public Ident getIdent() {
        return ident;
    }
}
