package model.unit;

import error.CompileError;
import lexer.Lexer;
import model.token.Number;
import model.token.Token;
import model.token.TokenType;

import java.io.IOException;

public class Term extends Expr {

    @Override
    public IExpr build() throws CompileError, IOException {
        boolean positive = true;
        TokenType[] initTypes = new TokenType[] { TokenType.Plus, TokenType.Sub, TokenType.LPar, TokenType.Number };
        Token token = this.require(initTypes);
        while (token.getType().equals(TokenType.Plus) || token.getType().equals(TokenType.Sub)) {
            if (token.getType().equals(TokenType.Sub))
                positive = !positive;
            token = this.require(initTypes);
        }
        if (token instanceof Number number) {
            return positive ? number : new Expr(new Number(0), TokenType.Sub, number);
        }
        else { // LPar
            IExpr expr = new Expr().build();
            this.require(TokenType.RPar);
            return positive ? expr : new Expr(new Number(0), TokenType.Sub, expr);
        }
    }

}
