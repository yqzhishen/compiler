package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import error.SyntaxError;
import lexer.Lexer;
import model.ir.Instruction;
import model.ir.Load;
import model.ir.Operand;
import model.ir.Store;
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

public class Stmt extends Sentence {

    private Ident leftVal;

    private IExpr expr;

    public Ident getLeftVal() {
        return this.leftVal;
    }

    public IExpr getExpr() {
        return this.expr;
    }

    @Override
    public Stmt build() throws CompileError, IOException {
        TokenType type = this.lexer.nextType();
        switch (type) {
            case Add, Sub, LPar, Number -> {
                this.expr = new Expr().build();
                this.require(TokenType.Semicolon);
                return this;
            }
            case Ident -> {
                TokenType aheadType = this.lexer.nextType(1);
                if (TokenType.Assign.equals(aheadType)) {
                    this.leftVal = (Ident) this.lexer.getToken();
                    this.lexer.getToken();
                }
                this.expr = new Expr().build();
                this.require(TokenType.Semicolon);
                return this;
            }
        }
        throw new SyntaxError(
                Lexer.getReader().getPos(),
                new TokenType[] {
                        TokenType.Add,
                        TokenType.Sub,
                        TokenType.LPar,
                        TokenType.Number,
                        TokenType.Ident,
                },
                type
        );
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Operand address = null;
        if (leftVal != null) {
            Symbol leftSym = table.get(leftVal, SymbolType.Variable);
            if (leftSym instanceof Variable variable) {
                address = variable.getAddress();
            }
            else {
                throw new SemanticError(leftVal.getPos(), "constant value '" + leftVal.getName() + "' cannot be assigned");
            }
        }
        if (expr instanceof Number number) {
            if (leftVal != null) {
                Store store = new Store("i32", new Operand(false, number.getValue()), "i32*", address);
                instructions.add(store);
            }
        }
        else if (expr instanceof Ident ident) {
            Symbol sym = table.get(ident, SymbolType.Variable);
            if (leftVal != null) {
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
        }
        else if (expr instanceof Expr expression) {
            instructions.addAll(expression.dump());
            if (leftVal != null) {
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
