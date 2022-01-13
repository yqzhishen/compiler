package model.unit;

import analyzer.CondFlow;
import analyzer.Tagger;
import error.CompileError;
import model.ir.Instruction;
import model.ir.Jump;
import model.ir.Label;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class IfClause extends Sentence {

    private Cond condition;

    private Block ifBlock;

    private Block elseBlock;

    @Override
    public IfClause build() throws CompileError {
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
        CondFlow flow = CondFlow.getInstance();
        Label labelIfTrue = new Label();
        Label labelIfFalse = new Label();
        Label labelPass = new Label();
        if (elseBlock != null) {
            flow.push(labelIfTrue, labelIfFalse);
        }
        else {
            flow.push(labelIfTrue, labelPass);
        }
        List<Instruction> instructions = new ArrayList<>(condition.generateIr());
        labelIfTrue.setTag(Tagger.newTag());
        List<Instruction> instructionsIfTrue = ifBlock.generateIr();
        List<Instruction> instructionsIfFalse = new ArrayList<>(0);
        if (elseBlock != null) {
            labelIfFalse.setTag(Tagger.newTag());
            instructionsIfFalse = elseBlock.generateIr();
        }
        labelPass.setTag(Tagger.newTag());
        if (instructionsIfTrue.isEmpty()) {
            instructionsIfTrue.add(new Jump(labelPass));
        }
        else switch (instructionsIfTrue.get(instructionsIfTrue.size() - 1).getType()) {
            case Br, Ret -> {}
            default -> instructionsIfTrue.add(new Jump(labelPass));
        }
        Jump jump = new Jump(condition.getResult(), flow.pass(!condition.isInverted()), flow.pass(condition.isInverted()));
        if (elseBlock != null) {
            if (instructionsIfFalse.isEmpty()) {
                instructionsIfFalse.add(new Jump(labelPass));
            }
            else switch (instructionsIfFalse.get(instructionsIfFalse.size() - 1).getType()) {
                case Br, Ret -> {}
                default -> instructionsIfFalse.add(new Jump(labelPass));
            }
        }
        flow.pop();
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

    private Block buildSentence() throws CompileError {
        if (TokenType.LBrace.equals(this.lexer.nextType())) {
            return new Block().build();
        }
        else {
            return new Block(new Sentence().build());
        }
    }

}
