package model.unit;

import analyzer.LoopStack;
import error.CompileError;
import error.SemanticError;
import model.ir.Instruction;
import model.ir.Jump;
import model.ir.Label;
import model.token.Token;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class LoopJump extends Sentence {

    private Token token;

    @Override
    public LoopJump build() throws CompileError {
        this.token = this.require(TokenType.Continue, TokenType.Break);
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>(1);
        LoopStack stack = LoopStack.getInstance();
        switch (token.getType()) {
            case Continue -> {
                Label head = stack.head();
                if (head == null)
                    throw new SemanticError(token.getPos(), "'continue' outside of loop");
                instructions.add(new Jump(head));
            }
            case Break -> {
                Label tail = stack.tail();
                if (tail == null)
                    throw new SemanticError(token.getPos(), "'break' outside of loop");
                instructions.add(new Jump(tail));
            }
        }
        return instructions;
    }

}
