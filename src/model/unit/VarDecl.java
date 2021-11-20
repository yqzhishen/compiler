package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.symbol.Const;
import model.symbol.Symbol;
import model.symbol.SymbolType;
import model.symbol.Variable;
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
        Ident ident = (Ident) this.require(TokenType.Ident);
        IExpr expr = null;
        if (TokenType.Assign.equals(this.lexer.nextType())) {
            this.lexer.getToken();
            expr = new Expr().build();
        }
        this.symbols.add(new Variable(ident, expr));
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            ident = (Ident) this.require(TokenType.Ident);
            expr = null;
            if (TokenType.Assign.equals(this.lexer.nextType())) {
                this.lexer.getToken();
                expr = new Expr().build();
            }
            this.symbols.add(new Variable(ident, expr));
        }
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public List<Instruction> dump() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        for (Symbol symbol : this.symbols) {
            Variable variable = (Variable) symbol;
            Operand address = new Operand(true, Tagger.newTag());
            Allocate allocate = new Allocate("i32", address);
            instructions.add(allocate);
            variable.setAddress(address);
            table.put(symbol);
            IExpr expr = variable.getExpr();
            if (expr instanceof Number number) {
                Store store = new Store("i32", new Operand(false, number.getValue()), "i32*", address);
                instructions.add(store);
            }
            else if (expr instanceof Ident ident) {
                Symbol sym = table.get(ident, SymbolType.Variable);
                if (sym instanceof Const rConst) {
                    Store store = new Store("i32", new Operand(false, rConst.getValue()), "i32*", address);
                    instructions.add(store);
                }
                else if (sym instanceof Variable rVar) {
                    Operand tmp = new Operand(true, Tagger.newTag());
                    Load load = new Load("i32", tmp, "i32 *", rVar.getAddress());
                    Store store = new Store("i32", tmp, "i32*", address);
                    instructions.add(load);
                    instructions.add(store);
                }
            }
            else if (expr instanceof Expr expression) {
                instructions.addAll(expression.dump());
                if (expression instanceof FuncCall call && expression.getResult() == null) {
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                }
                Store store = new Store("i32", expression.getResult(), "i32*", address);
                instructions.add(store);
            }
        }
        return instructions;
    }

}
