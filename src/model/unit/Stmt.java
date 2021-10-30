package model.unit;

import error.CompileError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.Ident;
import model.token.TokenType;

import java.io.IOException;

public class Stmt extends Sentence {

    private Ident leftVal;

    private IExpr expr;

    public Ident getLeftVal() {
        return this.leftVal;
    }

    public IExpr getExpr() {
        return this.expr;
    }

    @Override
    public Stmt build() throws CompileError, IOException {
        TokenType type = this.lexer.nextType();
        switch (type) {
            case Plus, Sub, LPar, Number -> {
                this.expr = new Expr().build();
                this.require(TokenType.Semicolon);
                return this;
            }
            case Ident -> {
                TokenType aheadType = this.lexer.nextType(1);
                if (TokenType.Assign.equals(aheadType)) {
                    this.leftVal = (Ident) this.lexer.getToken();
                    this.lexer.getToken();
                }
                this.expr = new Expr().build();
                this.require(TokenType.Semicolon);
                return this;
            }
        }
        throw new SyntaxError(
                Lexer.getReader().getPos(),
                new TokenType[] {
                        TokenType.Plus,
                        TokenType.Sub,
                        TokenType.LPar,
                        TokenType.Number,
                        TokenType.Ident,
                },
                type
        );
    }

}
