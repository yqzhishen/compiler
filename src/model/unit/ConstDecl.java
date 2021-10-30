package model.unit;

import error.CompileError;
import model.symbol.Const;
import model.token.Ident;
import model.token.TokenType;

import java.io.IOException;

public class ConstDecl extends Declare {

    @Override
    public ConstDecl build() throws IOException, CompileError {
        this.require(TokenType.Const);
        this.require(TokenType.Int);
        Ident ident = (Ident) this.require(TokenType.Ident);
        this.require(TokenType.Assign);
        this.symbols.add(new Const(ident, new Expr().build()));
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            ident = (Ident) this.require(TokenType.Ident);
            this.require(TokenType.Assign);
            this.symbols.add(new Const(ident, new Expr().build()));
        }
        this.require(TokenType.Semicolon);
        return this;
    }

}
