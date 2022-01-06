package model.unit;

import error.CompileError;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

public class Term extends Expr {

    private boolean inverted;

    private boolean negative;

    public Term() { }

    public Term(boolean bool) {
        this.bool = bool;
    }

    @Override
    public IExpr build() throws CompileError {
        this.buildUnaryOp();
        if (inverted) {
            Term term = new Term(true);
            term.negative = negative;
            return new NotCond(term.build());
        }
        if (negative) {
            return new Expr(new Number(0), TokenType.Sub, new Term().build());
        }
        IExpr expr;
        switch (this.lexer.nextType()) {
            case Number -> expr = (Number) this.lexer.getToken();
            case Ident -> {
                TokenType type = this.lexer.nextType(1);
                if (TokenType.LPar.equals(type))
                    expr = new FuncCall().build();
                else if (TokenType.LBracket.equals(type))
                    expr = new ArrayElement().build();
                else
                    expr = (Ident) this.lexer.getToken();
            }
            case LPar -> {
                this.lexer.getToken();
                expr = bool ? new Cond().build() : new Expr().build();
                this.require(TokenType.RPar);
            }
            default -> {
                // Just for throwing an exception
                this.require(TokenType.Add, TokenType.Sub, TokenType.Number, TokenType.Ident, TokenType.LPar);
                return null; // This shall never happen
            }
        }
        return expr;
    }

    private void buildUnaryOp() throws CompileError {
        TokenType nextType = this.lexer.nextType();
        boolean stop = false;
        while (!stop && nextType != null) {
            switch (nextType) {
                case Not -> inverted = !inverted;
                case Add -> {}
                case Sub -> negative = !negative;
                default -> stop = true;
            }
            if (!stop) {
                this.lexer.getToken();
                nextType = this.lexer.nextType();
            }
        }
    }

}
