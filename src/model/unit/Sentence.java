package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class Sentence extends AbstractUnit {

    @Override
    public Sentence build() throws IOException, CompileError {
        TokenType type = this.lexer.nextType();
        switch (type) {
            case Plus, Sub, LPar, Number, Ident -> {
                return new Stmt().build();
            }
            case Const, Int -> {
                return new Declare().build();
            }
            case If -> {
                return new IfClause().build();
            }
            case Return -> {
                return new Return().build();
            }
        }
        // Just for throwing an exception
        this.require(TokenType.Plus, TokenType.Sub, TokenType.LPar, TokenType.Number,
                TokenType.Ident, TokenType.Const, TokenType.Int, TokenType.If, TokenType.Return);
        return null; // This shall never happen
    }

    @Override
    public String dump() {
        return null;
    }

}
