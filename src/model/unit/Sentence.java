package model.unit;

import error.CompileError;
import error.SyntaxError;
import lexer.Lexer;
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
            case Const -> {
                return new ConstDecl().build();
            }
/*
            case Int -> {
                return new ConstDecl().build();
            }
*/
            case Return -> {
                return new Return().build();
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
                        TokenType.Const,
                        TokenType.Int,
                        TokenType.Return
                },
                type
        );
    }

    @Override
    public String dump() {
        return null;
    }

}
