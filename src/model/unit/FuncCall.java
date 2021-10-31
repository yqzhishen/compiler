package model.unit;

import error.CompileError;
import model.token.Ident;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr implements IExpr {

    private Ident ident;

    private final List<IExpr> params = new ArrayList<>();

    public Ident getIdent() {
        return this.ident;
    }

    public List<IExpr> getParams() {
        return this.params;
    }

    @Override
    public IExpr build() throws IOException, CompileError {
        this.ident = (Ident) this.require(TokenType.Ident);
        this.require(TokenType.LPar);
        if (!TokenType.RPar.equals(this.lexer.nextType())) {
            this.params.add(new Expr().build());
            while (!TokenType.RPar.equals(this.lexer.nextType())) {
                this.require(TokenType.Comma);
                this.params.add(new Expr().build());
            }
        }
        this.require(TokenType.RPar);
        return this;
    }

}
