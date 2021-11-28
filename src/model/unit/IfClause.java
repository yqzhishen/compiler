package model.unit;

import analyzer.Tagger;
import error.CompileError;
import model.ir.Instruction;
import model.ir.Jump;
import model.ir.Label;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IfClause extends Sentence {

    private Cond condition;

    private Block ifBlock;

    private Block elseBlock;

    @Override
    public IfClause build() throws IOException, CompileError {
        this.require(TokenType.If);
        this.require(TokenType.LPar);
        this.condition = (Cond) new Cond().build();
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
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>(condition.generateIr());
        Label labelIfTrue = new Label(Tagger.newTag());
        List<Instruction> instructionsIfTrue = ifBlock.generateIr();
        Label labelIfFalse = null;
        List<Instruction> instructionsIfFalse = new ArrayList<>(0);
        if (elseBlock != null) {
            labelIfFalse = new Label(Tagger.newTag());
            instructionsIfFalse = elseBlock.generateIr();
        }
        Label labelPass = new Label(Tagger.newTag());
        switch (instructionsIfTrue.get(instructionsIfTrue.size() - 1).getType()) {
            case Br, Ret -> {}
            default -> instructionsIfTrue.add(new Jump(labelPass));
        }
        Jump jump;
        if (elseBlock != null) {
            jump = new Jump(condition.getResult(), labelIfTrue, labelIfFalse);
            switch (instructionsIfFalse.get(instructionsIfFalse.size() - 1).getType()) {
                case Br, Ret -> {}
                default -> instructionsIfFalse.add(new Jump(labelPass));
            }
        }
        else {
            jump = new Jump(condition.getResult(), labelIfTrue, labelPass);
        }
        instructions.add(jump);
        instructions.add(labelIfTrue);
        instructions.addAll(instructionsIfTrue);
        if (elseBlock != null) {
            instructions.add(labelIfFalse);
            instructions.addAll(instructionsIfFalse);
        }
        instructions.add(labelPass);
        return instructions;
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
