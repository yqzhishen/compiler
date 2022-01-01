package analyzer;

import error.SemanticError;
import model.symbol.Array;
import model.symbol.Symbol;
import model.symbol.SymbolType;
import model.token.Ident;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymTable {

    private static final SymTable table = new SymTable();

    public static SymTable getInstance() {
        return table;
    }

    private final Stack<Map<String, Symbol>> valTable = new Stack<>();

    private final Map<String, Symbol> funcTable = new HashMap<>();

    private SymTable() {
        pushLayer();
    }

    public void pushLayer() {
        valTable.push(new HashMap<>());
    }

    public void popLayer() {
        valTable.pop();
    }

    public void put(Symbol symbol) throws SemanticError {
        String name = symbol.getIdent().getName();
        if (symbol.getType().equals(SymbolType.Function)) {
            if (this.funcTable.put(name, symbol) != null)
                throw new SemanticError(symbol.getIdent().getPos(), "function '" + name + "' already defined");
        }
        else if (this.valTable.peek().put(name, symbol) != null)
            throw new SemanticError(symbol.getIdent().getPos(), "symbol '" + name + "' already defined");
    }

    public Symbol get(Ident ident, SymbolType type) throws SemanticError {
        String name = ident.getName();
        switch (type) {
            case Const, Variable, Array -> {
                for (int i = this.valTable.size() - 1; i >= 0; --i) {
                    Symbol symbol = this.valTable.get(i).get(name);
                    if (symbol == null)
                        continue;
                    if (type.equals(SymbolType.Const) && !type.equals(symbol.getType()))
                        throw new SemanticError(ident.getPos(), '\'' + name + "' is not a constant value");
                    if (type.equals(SymbolType.Array) && !type.equals(symbol.getType()))
                        throw new SemanticError(ident.getPos(), '\'' + name + "' is not an array");
                    return symbol;
                }
                throw new SemanticError(ident.getPos(), "unresolved symbol '" + name + "'");
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
