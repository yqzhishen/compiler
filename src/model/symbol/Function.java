package model.symbol;

import model.token.Ident;
import model.unit.Argument;

import java.util.List;

public class Function extends Symbol {

    private final boolean isVoid;

    private final List<Argument> arguments;

    public Function(boolean isVoid, Ident ident, List<Argument> arguments) {
        super(ident);
        this.arguments = arguments;
        this.isVoid = isVoid;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public boolean isVoid() {
        return this.isVoid;
    }

    @Override
    public SymbolType getType() {
        return SymbolType.Function;
    }
}
