package model.unit;

import error.CompileError;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;

public class Term extends Expr {

    @Override
    public IExpr build() throws CompileError, IOException {
        if (inverted()) {
            return new NotCond(new Term().build());
        }
        if (!positive()) {
            return new Expr(new Number(0), TokenType.Sub, new Term().build());
        }
        IExpr expr;
        switch (this.lexer.nextType()) {
            case Number -> expr = (Number) this.lexer.getToken();
            case Ident -> {
                if (TokenType.LPar.equals(this.lexer.nextType(1)))
                    expr = new FuncCall().build();
                else
                    expr = (Ident) this.lexer.getToken();
            }
            case LPar -> {
                this.lexer.getToken();
                expr = new Expr().build();
                this.require(TokenType.RPar);
            }
            default -> {
                // Just for throwing an exception
                this.require(TokenType.Plus, TokenType.Sub, TokenType.Number, TokenType.Ident, TokenType.LPar);
                return null; // This shall never happen
            }
        }
        return expr;
    }

    private boolean inverted() throws CompileError, IOException {
        boolean inverted = false;
        TokenType nextType = this.lexer.nextType();
        while (TokenType.Not.equals(nextType)) {
            this.lexer.getToken();
            inverted = !inverted;
            nextType = this.lexer.nextType();
        }
        return inverted;
    }

    private boolean positive() throws CompileError, IOException {
        boolean positive = true;
        TokenType nextType = this.lexer.nextType();
        while (TokenType.Plus.equals(nextType) || TokenType.Sub.equals(nextType)) {
            this.lexer.getToken();
            if (nextType.equals(TokenType.Sub))
                positive = !positive;
            nextType = this.lexer.nextType();
        }
        return positive;
    }

}
