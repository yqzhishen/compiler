package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.symbol.*;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArrayElement extends Expr implements IExpr {

    private Ident ident;

    private final List<IExpr> indexes = new ArrayList<>();

    public Ident getIdent() {
        return ident;
    }

    @Override
    public ArrayElement build() throws IOException, CompileError {
        this.ident = (Ident) this.require(TokenType.Ident);
        do {
            this.require(TokenType.LBracket);
            this.indexes.add(new Expr().build());
            this.require(TokenType.RBracket);
        } while (TokenType.LBracket.equals(lexer.nextType()));
        return this;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = this.generateIrOfAddress();
        Operand value = Operand.local(Tagger.newTag());
        instructions.add(new Load("i32", value, "i32*", this.result));
        this.result = value;
        return instructions;
    }

    public List<Instruction> generateIrOfAddress() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Array array = (Array) table.get(ident, SymbolType.Array);
        List<IExpr> shape = array.getShape();
        if (indexes.size() < shape.size()) {
            throw new SemanticError(ident.getPos(), "too few indexes for array '" + ident.getName() + "'");
        }
        else if (indexes.size() > shape.size()) {
            throw new SemanticError(ident.getPos(), "too much indexes for array '" + ident.getName() + "'");
        }
        List<Operand> indexes = new ArrayList<>();
        indexes.add(Operand.number(0));
        for (IExpr index : this.indexes) {
            if (index instanceof Number number) {
                indexes.add(Operand.number(number.getValue()));
            }
            else if (index instanceof Ident ident) {
                Symbol symbol = this.table.get(ident, SymbolType.Variable);
                if (symbol instanceof Const pConst) {
                    indexes.add(Operand.number(pConst.getValue()));
                }
                else if (symbol instanceof Variable rVar) {
                    Operand tmp = Operand.local(Tagger.newTag());
                    Load load = new Load("i32", tmp, "i32*", rVar.getAddress());
                    instructions.add(load);
                    indexes.add(tmp);
                }
            }
            else if (index instanceof Expr expression) {
                instructions.addAll(expression.generateIr());
                if (expression instanceof FuncCall call && expression.getResult() == null) {
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                }
                indexes.add(expression.getResult());
            }
        }
        Operand result = Operand.local(Tagger.newTag());
        instructions.add(new GetElementPtr(result, array.getShapeToString(), array.getAddress(), indexes));
        this.result = result;
        return instructions;
    }

    @Override
    public Integer calculate() throws CompileError {
        throw new SemanticError(ident.getPos(), "not a constant value");
    }

}
