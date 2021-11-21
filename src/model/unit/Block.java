package model.unit;

import error.CompileError;
import lexer.Lexer;
import model.ir.Instruction;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block extends AbstractUnit {

    public Block() {
        this.sentences = new ArrayList<>();
    }

    public Block(Sentence singleSentence) {
        this.sentences = new ArrayList<>(1);
        this.sentences.add(singleSentence);
    }

    private final List<Sentence> sentences;

    @Override
    public Block build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        while (!Lexer.getLexer().nextType().equals(TokenType.RBrace)) {
            this.sentences.add(buildSentence());
        }
        this.require(TokenType.RBrace);
        return this;
    }

    public Sentence buildSentence() throws IOException, CompileError {
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

    public List<Instruction> dump() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        for (Sentence sentence : sentences) {
            instructions.addAll(sentence.dump());
        }
        return instructions;
    }

}
