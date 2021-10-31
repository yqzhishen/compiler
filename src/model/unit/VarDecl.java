package model.unit;

import error.CompileError;
import model.symbol.Variable;
import model.token.Ident;
import model.token.TokenType;

import java.io.IOException;

public class VarDecl extends Declare {

    @Override
    public VarDecl build() throws IOException, CompileError {
        this.require(TokenType.Int);
        Ident ident = (Ident) this.require(TokenType.Ident);
        IExpr expr = null;
        if (TokenType.Assign.equals(this.lexer.nextType())) {
            this.lexer.getToken();
            expr = new Expr().build();
        }
        this.symbols.add(new Variable(ident, expr));
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            ident = (Ident) this.require(TokenType.Ident);
            expr = null;
            if (TokenType.Assign.equals(this.lexer.nextType())) {
                this.lexer.getToken();
                expr = new Expr().build();
            }
            this.symbols.add(new Variable(ident, expr));
        }
        this.require(TokenType.Semicolon);
        return this;
    }
}
