package model.unit;

import error.CompileError;
import lexer.Lexer;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;

public class Block extends AbstractUnit {

    private final ArrayList<Sentence> sentences = new ArrayList<>();

    public ArrayList<Sentence> getSentences() {
        return this.sentences;
    }

    @Override
    public Block build() throws IOException, CompileError {
        this.require(TokenType.LBrace);
        while (!Lexer.getLexer().nextType().equals(TokenType.RBrace)) {
            this.sentences.add(new Return().build());
        }
        this.require(TokenType.RBrace);
        return this;
    }

    @Override
    public String dump() {
        return "{\n    " + this.sentences.get(0).dump() + "\n}";
    }

}
