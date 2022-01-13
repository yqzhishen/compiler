package model.unit;

import analyzer.CondFlow;
import analyzer.LoopScope;
import analyzer.Tagger;
import error.CompileError;
import model.ir.Instruction;
import model.ir.Jump;
import model.ir.Label;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class WhileClause extends Sentence {

    private Cond condition;

    private Block loopBlock;

    @Override
    public WhileClause build() throws CompileError {
        this.require(TokenType.While);
        this.require(TokenType.LPar);
        this.condition = (Cond) new Cond().build();
        this.require(TokenType.RPar);
        this.loopBlock = buildSentence();
        return this;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        LoopScope scope = LoopScope.getInstance();
        CondFlow flow = CondFlow.getInstance();
        Label head = new Label(Tagger.newTag());
        Label entry = new Label();
        Label tail = new Label();
        scope.pushLayer(head, tail);
        flow.push(entry, tail);
        instructions.add(new Jump(head));
        instructions.add(head);
        instructions.addAll(condition.generateIr());
        entry.setTag(Tagger.newTag());
        instructions.add(new Jump(condition.getResult(), flow.pass(!condition.isInverted()), flow.pass(condition.isInverted())));
        flow.pop();
        instructions.add(entry);
        instructions.addAll(loopBlock.generateIr());
        switch (instructions.get(instructions.size() - 1).getType()) {
            case Br, Ret -> {}
            default -> instructions.add(new Jump(head));
        }
        tail.setTag(Tagger.newTag());
        instructions.add(tail);
        scope.popLayer();
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
