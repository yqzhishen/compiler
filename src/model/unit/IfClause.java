package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class IfClause extends Sentence {

    private IExpr condition;

    private Block ifBlock;

    private Block elseBlock;

    public IExpr getCondition() {
        return condition;
    }

    public boolean hasElse() {
        return elseBlock != null;
    }

    public Block getIfBlock() {
        return ifBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    @Override
    public IfClause build() throws IOException, CompileError {
        this.require(TokenType.If);
        this.require(TokenType.LPar);
        this.condition = new Cond().build();
        this.require(TokenType.RPar);
        this.ifBlock = buildSentence();
        if (TokenType.Else.equals(this.lexer.nextType())) {
            this.lexer.getToken();
            if (TokenType.If.equals(this.lexer.nextType())) {
                this.elseBlock = new Block(new IfClause().build());
            }
            else {
                this.elseBlock = buildSentence();
            }
        }
        return this;
    }

    @Override
    public String dump() {
        return null;
    }

    private Block buildSentence() throws CompileError, IOException {
        if (TokenType.LBrace.equals(this.lexer.nextType())) {
            return new Block().build();
        }
        else {
            return new Block(new Sentence().build());
        }
    }

}
