package model.symbol;

import model.token.Ident;

public class Function extends Symbol {

    private final int schema;

    private final boolean isVoid;

    public Function(Ident ident, int schema, boolean isVoid) {
        super(ident);
        this.schema = schema;
        this.isVoid = isVoid;
    }

    public int getSchema() {
        return this.schema;
    }

    public boolean isVoid() {
        return this.isVoid;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Function;
    }
}
