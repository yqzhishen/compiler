package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.ir.Call.Param;
import model.symbol.Array;
import model.symbol.Const;
import model.symbol.Symbol;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstDecl extends Declare {

    @Override
    public ConstDecl build() throws IOException, CompileError {
        this.require(TokenType.Const);
        this.require(TokenType.Int);
        this.symbols.add(this.buildInitializer());
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            this.symbols.add(this.buildInitializer());
        }
        this.require(TokenType.Semicolon);
        return this;
    }

    private Symbol buildInitializer() throws IOException, CompileError {
        Ident ident = (Ident) this.require(TokenType.Ident);
        boolean isArray = TokenType.LBracket.equals(lexer.nextType());
        List<IExpr> shape = null;
        if (isArray) {
            shape = new ArrayList<>();
            do {
                lexer.getToken();
                shape.add(new Expr().build());
                this.require(TokenType.RBracket);
            } while (TokenType.LBracket.equals(lexer.nextType()));
        }
        this.require(TokenType.Assign);
        if (isArray) {
            ArrayInitializer initializer = (ArrayInitializer) ArrayInitializer.local(shape.size()).build();
            return Array.constArray(ident, shape, initializer);
        }
        else {
            return new Const(ident, new Expr().build());
        }
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        for (Symbol symbol : this.symbols) {
            if (symbol instanceof Const constant) {
                IExpr expr = constant.getExpr();
                constant.setValue(expr.calculate());
                this.table.put(constant);
            }
            else if (symbol instanceof Array array) {
                List<IExpr> shape = array.getShape();
                for (int i = 0; i < shape.size(); ++i) {
                    shape.set(i, new Number(shape.get(i).calculate()));
                }
                String shapeToString = array.getShapeToString();
                Operand address = Operand.reg(Tagger.newTag());
                Allocate allocate = new Allocate(shapeToString, address);
                instructions.add(allocate);
                array.setAddress(address);
                this.table.put(array);
                int size = 1;
                List<Operand> indexes = new ArrayList<>();
                Operand zero = Operand.number(0);
                indexes.add(zero);
                for (IExpr dim : shape) {
                    size *= ((Number) dim).getValue();
                    indexes.add(zero);
                }
                Operand head = Operand.reg(Tagger.newTag());
                instructions.add(new GetElementPtr(head, shapeToString, address, indexes));
                instructions.add(
                        new Call("memset", List.of(
                                new Param("i32*", head),
                                new Param("i32", Operand.number(0)),
                                new Param("i32", Operand.number(size * 4)))));
                try {
                    instructions.addAll(initializeArray(head, size, 0, shape, array.getInitializer()));
                }
                catch (IllegalStateException e) {
                    throw new SemanticError(array.getIdent().getPos(), "initializer index out of bound");
                }
            }
        }
        return instructions;
    }

    private List<Instruction> initializeArray(Operand head, int size, int offset, List<IExpr> shape, IArrayInitializer initializer) throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        int length = ((Number) shape.get(0)).getValue();
        List<IArrayInitializer> initializers = initializer.initializersOfThisDimension();
        if (initializers.size() > length) {
            throw new IllegalStateException();
        }
        int step = size / length;
        for (int index = 0; index < length && index < initializers.size(); ++index) {
            if (shape.size() == 1) {
                Operand target = Operand.reg(Tagger.newTag());
                instructions.add(new GetElementPtr(target, "i32", head, List.of(Operand.number(offset + index * step))));
                instructions.add(new Store("i32", Operand.number(((IExpr) initializers.get(index)).calculate()), "i32*", target));
            }
            else {
                instructions.addAll(initializeArray(head, step, offset + index * step, shape.subList(1, shape.size()), initializers.get(index)));
            }
        }
        return instructions;
    }

}
