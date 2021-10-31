package analyzer;

import error.SemanticError;
import model.symbol.Symbol;
import model.symbol.SymbolType;
import model.token.Ident;

import java.util.HashMap;
import java.util.Map;

public class SymTable {

    private static final SymTable table = new SymTable();

    public static SymTable getSymTable() {
        return table;
    }

    private final Map<String, Symbol> valTable = new HashMap<>();

    private final Map<String, Symbol> funcTable = new HashMap<>();

    private SymTable() { }

    public void put(Symbol symbol) throws SemanticError {
        String name = symbol.getIdent().getName();
        if (symbol.getType().equals(SymbolType.Function)) {
            if (this.funcTable.put(name, symbol) != null)
                throw new SemanticError(symbol.getIdent().getPos(), "function '" + name + "' already defined");
        }
        else if (this.valTable.put(name, symbol) != null)
            throw new SemanticError(symbol.getIdent().getPos(), "symbol '" + name + "' already defined");
    }

    public Symbol get(Ident ident, SymbolType type) throws SemanticError {
        String name = ident.getName();
        switch (type) {
            case Const, Variable -> {
                Symbol symbol = this.valTable.get(name);
                if (symbol == null)
                    throw new SemanticError(ident.getPos(), "unresolved symbol '" + name + "'");
                if (type.equals(SymbolType.Const) && !type.equals(symbol.getType()))
                    throw new SemanticError(ident.getPos(), "not a constant value");
                return symbol;
            }
            case Function -> {
                Symbol symbol = this.funcTable.get(name);
                if (symbol == null)
                    throw new SemanticError(ident.getPos(), "unresolved symbol '" + name + "'");
                return symbol;
            }
            default -> throw new IllegalArgumentException();
        }
    }

}
