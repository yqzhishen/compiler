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

public class VarDecl extends Declare {

    @Override
    public VarDecl build() throws IOException, CompileError {
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
        IExpr expr = null;
        List<IExpr> shape = null;
        ArrayInitializer initializer = null;
        if (isArray) {
            shape = new ArrayList<>();
            do {
                lexer.getToken();
                shape.add(new Expr().build());
                this.require(TokenType.RBracket);
            } while (TokenType.LBracket.equals(lexer.nextType()));
        }
        if (TokenType.Assign.equals(this.lexer.nextType())) {
            this.lexer.getToken();
            if (isArray) {
                initializer = (ArrayInitializer) ArrayInitializer.local(shape.size()).build();
            }
            else {
                expr = new Expr().build();
            }
        }
        if (isArray)
            return Array.varArray(ident, shape, initializer);
        else
            return new Variable(ident, expr);
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        for (Symbol symbol : this.symbols) {
            if (symbol instanceof Variable variable) {
                Operand address = Operand.local(Tagger.newTag());
                Allocate allocate = new Allocate("i32", address);
                instructions.add(allocate);
                variable.setAddress(address);
                table.put(symbol);
                IExpr expr = variable.getExpr();
                if (expr == null)
                    continue;
                instructions.addAll(storeExpr(address, expr));
            }
            else if (symbol instanceof Array array) {
                List<IExpr> shape = array.getShape();
                for (int i = 0; i < shape.size(); ++i) {
                    int size = shape.get(i).calculate();
                    if (size < 0)
                        throw new SemanticError(array.getIdent().getPos(), "array size must not be negative");
                    shape.set(i, new Number(size));
                }
                String shapeToString = array.getShapeToString();
                Operand address = Operand.local(Tagger.newTag());
                Allocate allocate = new Allocate(shapeToString, address);
                instructions.add(allocate);
                array.setAddress(address);
                this.table.put(array);
                ArrayInitializer initializer = array.getInitializer();
                if (initializer == null)
                    continue;
                int size = 1;
                List<Operand> indexes = new ArrayList<>();
                Operand zero = Operand.number(0);
                indexes.add(zero);
                for (IExpr dim : shape) {
                    size *= ((Number) dim).getValue();
                    indexes.add(zero);
                }
                Operand head = Operand.local(Tagger.newTag());
                instructions.add(new GetElementPtr(head, shapeToString, address, indexes));
                instructions.add(
                        new Call("memset", List.of(
                                new Call.Param("i32*", head),
                                new Call.Param("i32", Operand.number(0)),
                                new Call.Param("i32", Operand.number(size * 4)))));
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

    private List<Instruction> storeExpr(Operand address, IExpr expr) throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        if (expr instanceof Number number) {
            Store store = new Store("i32", Operand.number(number.getValue()), "i32*", address);
            instructions.add(store);
        } else if (expr instanceof Ident ident) {
            Symbol sym = table.get(ident, SymbolType.Variable);
            if (sym instanceof Const rConst) {
                Store store = new Store("i32", Operand.number(rConst.getValue()), "i32*", address);
                instructions.add(store);
            } else if (sym instanceof Variable rVar) {
                Operand tmp = Operand.local(Tagger.newTag());
                Load load = new Load("i32", tmp, "i32*", rVar.getAddress());
                Store store = new Store("i32", tmp, "i32*", address);
                instructions.add(load);
                instructions.add(store);
            }
        } else if (expr instanceof Expr expression) {
            instructions.addAll(expression.generateIr());
            if (expression instanceof FuncCall call && expression.getResult() == null) {
                throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
            }
            Store store = new Store("i32", expression.getResult(), "i32*", address);
            instructions.add(store);
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
        int step = length == 0 ? 0 : size / length;
        for (int index = 0; index < length && index < initializers.size(); ++index) {
            if (shape.size() == 1) {
                Operand target = Operand.local(Tagger.newTag());
                instructions.add(new GetElementPtr(target, "i32", head, List.of(Operand.number(offset + index * step))));
                instructions.addAll(storeExpr(target, (IExpr) initializers.get(index)));
            }
            else {
                instructions.addAll(initializeArray(head, step, offset + index * step, shape.subList(1, shape.size()), initializers.get(index)));
            }
        }
        return instructions;
    }

}
