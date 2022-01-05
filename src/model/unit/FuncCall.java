package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.ir.Call.Param;
import model.symbol.*;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr implements IExpr {

    private Ident ident;

    private final List<IExpr> params = new ArrayList<>();

    public Ident getIdent() {
        return this.ident;
    }

    @Override
    public IExpr build() throws CompileError {
        this.ident = (Ident) this.require(TokenType.Ident);
        this.require(TokenType.LPar);
        if (!TokenType.RPar.equals(this.lexer.nextType())) {
            this.params.add(new Expr().build());
            while (!TokenType.RPar.equals(this.lexer.nextType())) {
                this.require(TokenType.Comma);
                this.params.add(new Expr().build());
            }
        }
        this.require(TokenType.RPar);
        return this;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Function function = (Function) table.get(ident, SymbolType.Function);
        List<Argument> arguments = function.getArguments();
        if (params.size() < arguments.size()) {
            throw new SemanticError(ident.getPos(), "too few arguments for function '" + ident.getName() + '\'');
        }
        else if (params.size() > arguments.size()) {
            throw new SemanticError(ident.getPos(), "too many arguments for function '" + ident.getName() + '\'');
        }
        List<Param> params = new ArrayList<>();
        for (int i = 0; i < this.params.size(); ++i) {
            IExpr param = this.params.get(i);
            Argument argument = arguments.get(i);
            if (param instanceof Number number) {
                if (argument.isPointer()) {
                    throw new SemanticError(ident.getPos(), "incompatible type (required pointer, got value)");
                }
                params.add(new Param("i32", Operand.number(number.getValue())));
            }
            else if (param instanceof Ident ident) {
                Symbol symbol = this.table.get(ident, SymbolType.Variable);
                if (symbol instanceof Array array) {
                    if (!argument.isPointer()) {
                        throw new SemanticError(this.ident.getPos(), "incompatible type (required value, got pointer)");
                    }
                    List<IExpr> argShape = argument.getShape();
                    List<IExpr> paramShape = array.getShape();
                    if (paramShape.size() < argShape.size() + 1) {
                        throw new SemanticError(this.ident.getPos(), "too few dimensions for array '" + array.getIdent().getName() + '\'');
                    }
                    else if (paramShape.size() > argShape.size() + 1) {
                        throw new SemanticError(this.ident.getPos(), "too many dimensions for array '" + array.getIdent().getName() + '\'');
                    }
                    for (int j = 0; j < argShape.size(); ++j) {
                        int argDim = ((Number) argShape.get(j)).getValue();
                        int paramDim = ((Number) paramShape.get(j + 1)).getValue();
                        if (paramDim != argDim) {
                            throw new SemanticError(this.ident.getPos(), String.format("incompatible shape (required %d, got %d)", argDim, paramDim));
                        }
                    }
                    if (((Number) paramShape.get(0)).getValue() != -1) {
                        Operand pointer = Operand.local(Tagger.newTag());
                        instructions.add(new GetElementPtr(pointer, array.getShapeToString(), array.getAddress(), List.of(Operand.number(0), Operand.number(0))));
                        params.add(new Param(Array.dumpShape(argShape) + "*", pointer));
                    }
                    else {
                        params.add(new Param(Array.dumpShape(argShape) + "*", array.getAddress()));
                    }
                }
                else {
                    if (argument.isPointer()) {
                        throw new SemanticError(this.ident.getPos(), "incompatible type (required pointer, got value)");
                    }
                    if (symbol instanceof Const pConst) {
                        params.add(new Param("i32", Operand.number(pConst.getValue())));
                    } else if (symbol instanceof Variable rVar) {
                        Operand tmp = Operand.local(Tagger.newTag());
                        Load load = new Load("i32", tmp, "i32*", rVar.getAddress());
                        instructions.add(load);
                        params.add(new Param("i32", tmp));
                    }
                }
            }
            else if (param instanceof ArrayElement element) {
                Array array = (Array) table.get(element.getIdent(), SymbolType.Array);
                List<IExpr> indexes = element.getIndexes();
                List<IExpr> arrayShape = array.getShape();
                if (indexes.size() > arrayShape.size()) {
                    throw new SemanticError(element.getIdent().getPos(), "too many indexes for array '" + ident.getName() + "'");
                }
                else if (indexes.size() == arrayShape.size()) {
                    if (argument.isPointer()) {
                        throw new SemanticError(ident.getPos(), "incompatible type (required pointer, got value)");
                    }
                    instructions.addAll(element.generateIr());
                    params.add(new Param("i32", element.getResult()));
                }
                else {
                    if (!argument.isPointer()) {
                        throw new SemanticError(ident.getPos(), "incompatible type (required value, got pointer)");
                    }
                    List<IExpr> argShape = argument.getShape();
                    List<IExpr> paramShape = arrayShape.subList(indexes.size(), arrayShape.size());
                    if (paramShape.size() < argShape.size() + 1) {
                        throw new SemanticError(this.ident.getPos(), "too few dimensions for array '" + array.getIdent().getName() + '\'');
                    }
                    else if (paramShape.size() > argShape.size() + 1) {
                        throw new SemanticError(this.ident.getPos(), "too many dimensions for array '" + array.getIdent().getName() + '\'');
                    }
                    for (int j = 0; j < argShape.size(); ++j) {
                        int argDim = ((Number) argShape.get(j)).getValue();
                        int paramDim = ((Number) paramShape.get(j + 1)).getValue();
                        if (paramDim != argDim) {
                            throw new SemanticError(this.ident.getPos(), String.format("incompatible shape (required %d, got %d)", argDim, paramDim));
                        }
                    }
                    element.setPartOfArray(true);
                    indexes.add(new Number(0));
                    instructions.addAll(element.generateIrOfAddress());
                    params.add(new Param(Array.dumpShape(argShape) + "*", element.getResult()));
                }
            }
            else if (param instanceof Expr expression) {
                if (argument.isPointer()) {
                    throw new SemanticError(ident.getPos(), "incompatible type (required pointer)");
                }
                instructions.addAll(expression.generateIr());
                if (expression instanceof FuncCall call && expression.getResult() == null) {
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void')");
                }
                params.add(new Param("i32", expression.getResult()));
            }
        }
        if (function.isVoid()) {
            Call call = new Call(function.getIdent().getName(), params);
            instructions.add(call);
        }
        else {
            Operand result = Operand.local(Tagger.newTag());
            Call call = new Call("i32", result, function.getIdent().getName(), params);
            this.result = result;
            instructions.add(call);
        }
        return instructions;
    }

    @Override
    public Integer calculate() throws CompileError {
        throw new SemanticError(ident.getPos(), "not a constant value");
    }

}
