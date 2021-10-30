package model.unit;

import error.CompileError;
import model.symbol.Symbol;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Declare extends Sentence {

    protected List<Symbol> symbols = new ArrayList<>();

    public List<Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public Declare build() throws IOException, CompileError {
        return switch (this.require(TokenType.Const, TokenType.Int).getType()) {
            case Const -> new ConstDecl().build();
            case Int -> new VarDecl().build();
            default -> null;
        };
    }

    @Override
    public String dump() {
        return null;
    }

}
