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
        switch (this.lexer.nextType()) {
            case Const -> {
                return new ConstDecl().build();
            }
            case Int -> {
                return new VarDecl().build();
            }
            default -> {
                // Just for throwing an exception
                this.require(TokenType.Const, TokenType.Int);
                return null; // This shall never happen
            }
        }
    }

    @Override
    public String dump() {
        return null;
    }

}