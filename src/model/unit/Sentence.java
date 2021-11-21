package model.unit;

import analyzer.SymTable;
import error.CompileError;
import model.ir.Instruction;
import model.token.TokenType;

import java.io.IOException;
import java.util.List;

public class Sentence extends AbstractUnit {

    protected SymTable table = SymTable.getSymTable();

    @Override
    public Sentence build() throws IOException, CompileError {
        TokenType type = this.lexer.nextType();
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
            case Return -> {
                return new Return().build();
            }
        }
        // Just for throwing an exception
        this.require(TokenType.Add, TokenType.Sub, TokenType.LPar, TokenType.Number,
                TokenType.Ident, TokenType.Const, TokenType.Int, TokenType.If, TokenType.Return);
        return null;
    }

    public List<Instruction> generateIr() throws CompileError {
        return null;
    }

}
