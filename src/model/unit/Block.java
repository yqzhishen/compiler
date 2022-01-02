package model.unit;

import error.CompileError;
import lexer.Lexer;
import model.ir.Instruction;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block extends Sentence {

    private final List<Sentence> sentences;

    private boolean isFuncBlock;

    public Block() {
        this.sentences = new ArrayList<>();
    }

    public Block(Sentence singleSentence) {
        this.sentences = new ArrayList<>(1);
        this.sentences.add(singleSentence);
    }

    public Block(boolean isFuncBlock) {
        this.sentences = new ArrayList<>();
        this.isFuncBlock = isFuncBlock;
    }

    @Override
    public Block build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        while (!TokenType.RBrace.equals(Lexer.getLexer().nextType())) {
            this.sentences.add(new Sentence().build());
        }
        this.require(TokenType.RBrace);
        return this;
    }

    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        if (!isFuncBlock) {
            this.table.pushLayer();
        }
        for (Sentence sentence : sentences) {
            instructions.addAll(sentence.generateIr());
        }
        if (!isFuncBlock) {
            this.table.popLayer();
        }
        return instructions;
    }

}
