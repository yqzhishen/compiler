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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr implements IExpr {

    private Ident ident;

    private final List<IExpr> params = new ArrayList<>();

    public Ident getIdent() {
        return this.ident;
    }

    public List<IExpr> getParams() {
        return this.params;
    }

    @Override
    public IExpr build() throws IOException, CompileError {
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
    public List<Instruction> dump() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Function function = (Function) table.get(ident, SymbolType.Function);
        if (params.size() < function.getSchema())
            throw new SemanticError(ident.getPos(), "too few arguments for function '" + ident.getName() + "'");
        else if (params.size() > function.getSchema())
            throw new SemanticError(ident.getPos(), "too much arguments for function '" + ident.getName() + "'");
        List<Param> params = new ArrayList<>();
        for (IExpr param : this.params) {
            if (param instanceof Number number) {
                params.add(new Param("i32", new Operand(false, number.getValue())));
            }
            else if (param instanceof Ident ident) {
                Symbol symbol = this.table.get(ident, SymbolType.Variable);
                if (symbol instanceof Const pConst) {
                    params.add(new Param("i32", new Operand(false, pConst.getValue())));
                }
                else if (symbol instanceof Variable rVar) {
                    Operand tmp = new Operand(true, Tagger.newTag());
                    Load load = new Load("i32", tmp, "i32 *", rVar.getAddress());
                    instructions.add(load);
                    params.add(new Param("i32", tmp));
                }
            }
            else if (param instanceof Expr expression) {
                instructions.addAll(expression.dump());
                if (expression instanceof FuncCall call && expression.getResult() == null) {
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                }
                params.add(new Param("i32", expression.getResult()));
            }
        }
        if (function.isVoid()) {
            Call call = new Call(function.getIdent().getName(), params);
            instructions.add(call);
        }
        else {
            Operand result = new Operand(true, Tagger.newTag());
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
