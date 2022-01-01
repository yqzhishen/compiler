package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.Instruction;
import model.ir.Load;
import model.ir.Operand;
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

public class Return extends Sentence {

    private IExpr expr;

    public IExpr getExpr() {
        return this.expr;
    }

    @Override
    public Return build() throws IOException, CompileError {
        this.require(TokenType.Return);
        this.expr = new Expr().build();
        this.require(TokenType.Semicolon);
        return this;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>(2);
        if (expr instanceof Number number) {
            model.ir.Return ret = new model.ir.Return("i32", new Operand(false, number.getValue()));
            instructions.add(ret);
        }
        else if (expr instanceof Ident ident) {
            Symbol symbol = this.table.get(ident, SymbolType.Variable);
            if (symbol instanceof Const rConst) {
                model.ir.Return ret = new model.ir.Return("i32", new Operand(false, rConst.getValue()));
                instructions.add(ret);
            }
            else if (symbol instanceof Variable rVar) {
                Operand tmp = new Operand(true, Tagger.newTag());
                Load load = new Load("i32", tmp, "i32*", rVar.getAddress());
                model.ir.Return ret = new model.ir.Return("i32", tmp);
                instructions.add(load);
                instructions.add(ret);
            }
        }
        else if (expr instanceof Expr expression) {
            instructions.addAll(expression.generateIr());
            if (expression instanceof FuncCall call && expression.getResult() == null) {
                throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
            }
            model.ir.Return ret = new model.ir.Return("i32", expression.getResult());
            instructions.add(ret);
        }
        return instructions;
    }
}
