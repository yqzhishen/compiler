package model.unit;

import analyzer.SymTable;
import error.CompileError;
import model.ir.Operand;
import model.symbol.Const;
import model.symbol.Symbol;
import model.symbol.Variable;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlobalDef extends AbstractUnit {

    private boolean isConst = false;

    private final List<Symbol> symbols = new ArrayList<>();

    public boolean isConst() {
        return isConst;
    }

    @Override
    public GlobalDef build() throws IOException, CompileError {
        if (TokenType.Const.equals(lexer.nextType())) {
            this.isConst = true;
            lexer.getToken();
        }
        this.require(TokenType.Int);
        Ident ident = (Ident) this.require(TokenType.Ident);
        IExpr expr;
        if (TokenType.Assign.equals(this.lexer.nextType())) {
            this.lexer.getToken();
            expr = new Expr().build();
        }
        else {
            expr = new Number(0);
        }
        Symbol symbol = this.isConst ? new Const(ident, expr) : new Variable(ident, expr);
        this.symbols.add(symbol);
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            ident = (Ident) this.require(TokenType.Ident);
            if (TokenType.Assign.equals(this.lexer.nextType())) {
                this.lexer.getToken();
                expr = new Expr().build();
            }
            else {
                expr = new Number(0);
            }
            symbol = this.isConst ? new Const(ident, expr) : new Variable(ident, expr);
            this.symbols.add(symbol);
        }
        this.require(TokenType.Semicolon);
        return this;
    }

    public String generateCode() throws CompileError {
        SymTable table = SymTable.getSymTable();
        if (this.isConst) {
            for (Symbol symbol : this.symbols) {
                Const constant = (Const) symbol;
                constant.setValue(constant.getExpr().calculate());
                table.put(constant);
            }
            return "";
        }
        else {
            StringBuilder builder = new StringBuilder();
            for (Symbol symbol : this.symbols) {
                Variable variable = (Variable) symbol;
                String name = variable.getIdent().getName();
                variable.setAddress(new Operand(name));
                table.put(variable);
                builder.append('@').append(name).append(" = dso_local global i32 ");
                builder.append(variable.getExpr().calculate()).append('\n');
            }
            return builder.toString();
        }
    }

}
