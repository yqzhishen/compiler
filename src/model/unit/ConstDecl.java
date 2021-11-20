package model.unit;

import error.CompileError;
import error.SemanticError;
import model.ir.Instruction;
import model.symbol.Const;
import model.symbol.Symbol;
import model.token.Ident;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstDecl extends Declare {

    @Override
    public ConstDecl build() throws IOException, CompileError {
        this.require(TokenType.Const);
        this.require(TokenType.Int);
        Ident ident = (Ident) this.require(TokenType.Ident);
        this.require(TokenType.Assign);
        this.symbols.add(new Const(ident, new Expr().build()));
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            ident = (Ident) this.require(TokenType.Ident);
            this.require(TokenType.Assign);
            this.symbols.add(new Const(ident, new Expr().build()));
        }
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public List<Instruction> dump() throws CompileError {
        for (Symbol symbol : this.symbols) {
            Const constant = (Const) symbol;
            IExpr expr = constant.getExpr();
            constant.setValue(expr.calculate());
            this.table.put(constant);
        }
        return new ArrayList<>(0);
    }

}
