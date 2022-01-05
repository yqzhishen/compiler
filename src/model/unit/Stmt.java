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
import model.symbol.*;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Stmt extends Sentence {

    private Ident leftVal;

    private ArrayElement leftEle;

    private IExpr expr;

    public IExpr getExpr() {
        return this.expr;
    }

    @Override
    public Stmt build() throws CompileError {
        TokenType type = this.lexer.nextType();
        switch (type) {
            case Add, Sub, LPar, Number -> {
                this.expr = new Expr().build();
                this.require(TokenType.Semicolon);
                return this;
            }
            case Ident -> {
                boolean isAssign = false;
                int i = 1;
                TokenType aheadType;
                do {
                    aheadType = this.lexer.nextType(i);
                    if (TokenType.Assign.equals(aheadType)) {
                        isAssign = true;
                        break;
                    }
                    ++i;
                } while (!TokenType.Semicolon.equals(aheadType));
                if (isAssign) {
                    aheadType = lexer.nextType(1);
                    if (TokenType.LBracket.equals(aheadType)) {
                        this.leftEle = new ArrayElement().build();
                    }
                    else {
                        this.leftVal = (Ident) this.lexer.getToken();
                    }
                    this.lexer.getToken(); // Assign
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
            else if (leftSym instanceof Array) {
                throw new SemanticError(leftVal.getPos(), "expected index for array '" + leftVal.getName() + '\'');
            }
            else {
                throw new SemanticError(leftVal.getPos(), "constant value '" + leftVal.getName() + "' cannot be assigned");
            }
        }
        else if (leftEle != null) {
            Array leftArr = (Array) table.get(leftEle.getIdent(), SymbolType.Array);
            if (leftArr.isConst()) {
                throw new SemanticError(leftEle.getIdent().getPos(), "element of constant array '" + leftEle.getIdent().getName() + "' cannot be assigned");
            }
            instructions.addAll(leftEle.generateIrOfAddress());
            address = leftEle.getResult();
        }
        if (expr instanceof Number number) {
            if (leftVal != null || leftEle != null) {
                Store store = new Store("i32", Operand.number(number.getValue()), "i32*", address);
                instructions.add(store);
            }
        }
        else if (expr instanceof Ident ident) {
            Symbol sym = table.get(ident, SymbolType.Variable);
            if (leftVal != null || leftEle != null) {
                if (sym instanceof Const rConst) {
                    Store store = new Store("i32", Operand.number(rConst.getValue()), "i32*", address);
                    instructions.add(store);
                }
                else if (sym instanceof Variable rVar) {
                    Operand tmp = Operand.local(Tagger.newTag());
                    Load load = new Load("i32", tmp, "i32*", rVar.getAddress());
                    Store store = new Store("i32", tmp, "i32*", address);
                    instructions.add(load);
                    instructions.add(store);
                }
            }
        }
        else if (expr instanceof Expr expression) {
            instructions.addAll(expression.generateIr());
            if (leftVal != null || leftEle != null) {
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
