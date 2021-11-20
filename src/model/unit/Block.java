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

    public List<Sentence> getSentences() {
        return this.sentences;
    }

    @Override
    public Block build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        while (!Lexer.getLexer().nextType().equals(TokenType.RBrace)) {
            this.sentences.add(new Sentence().build());
        }
        this.require(TokenType.RBrace);
        return this;
    }

    public List<Instruction> dump() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        for (Sentence sentence : sentences) {
            instructions.addAll(sentence.dump());
        }
        return instructions;
    }

}
