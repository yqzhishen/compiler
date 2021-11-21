package model.unit;

import analyzer.Tagger;
import error.CompileError;
import model.ir.Instruction;
import model.ir.InstructionType;
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
    public List<Instruction> dump() throws CompileError {
        List<Instruction> instructions = new ArrayList<>(condition.dump());
        int tagIfTrue = Tagger.newTag();
        Label labelIfTrue = new Label(tagIfTrue);
        List<Instruction> instructionsIfTrue = ifBlock.dump();
        int tagIfFalse = -1;
        Label labelIfFalse = null;
        List<Instruction> instructionsIfFalse = new ArrayList<>(0);
        if (elseBlock != null) {
            tagIfFalse = Tagger.newTag();
            labelIfFalse = new Label(tagIfFalse);
            instructionsIfFalse = elseBlock.dump();
        }
        int tagPass = Tagger.newTag();
        Label labelPass = new Label(tagPass);
        if (!instructionsIfTrue.get(instructionsIfTrue.size() - 1).getType().equals(InstructionType.Ret)) {
            instructionsIfTrue.add(new Jump(tagPass));
        }
        Jump jump;
        if (elseBlock != null) {
            jump = new Jump(condition.getResult(), tagIfTrue, tagIfFalse);
            if (!instructionsIfFalse.get(instructionsIfFalse.size() - 1).getType().equals(InstructionType.Ret)) {
                instructionsIfFalse.add(new Jump(tagPass));
            }
        }
        else {
            jump = new Jump(condition.getResult(), tagIfTrue, tagPass);
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
            Block block = new Block(null);
            Sentence sentence = block.buildSentence();
            return new Block(sentence);
        }
    }

}
