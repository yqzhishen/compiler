package model.unit;

import analyzer.SymTable;
import error.CompileError;
import model.ir.Instruction;
import model.token.TokenType;

import java.util.List;

public class Sentence extends AbstractUnit {

    protected SymTable table = SymTable.getInstance();

    @Override
    public Sentence build() throws CompileError {
        TokenType type = this.lexer.nextType();
        if (type != null) {
            switch (type) {
                case Add, Sub, LPar, Number, Ident -> {
                    return new Stmt().build();
                }
                case Const, Int -> {
                    return new Declare().build();
                }
                case If -> {
                    return new IfClause().build();
                }
                case While -> {
                    return new WhileClause().build();
                }
                case Continue, Break -> {
                    return new LoopJump().build();
                }
                case Return -> {
                    return new Return().build();
                }
                case Semicolon -> {
                    lexer.getToken();
                    return new EmptyStmt();
                }
                case LBrace -> {
                    return new Block().build();
                }
            }
        }
        // Just for throwing an exception
        this.require(TokenType.Add, TokenType.Sub, TokenType.LPar, TokenType.Number, TokenType.Ident, TokenType.Const, TokenType.Int,
                TokenType.If, TokenType.While, TokenType.Continue, TokenType.Break, TokenType.Return, TokenType.Semicolon, TokenType.LBrace);
        return null;
    }

    public List<Instruction> generateIr() throws CompileError {
        return null;
    }

}
