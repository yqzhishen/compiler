package model.unit;

import analyzer.Tagger;
import error.CompileError;
import error.SemanticError;
import model.ir.*;
import model.ir.Compare.CmpType;
import model.symbol.*;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class CmpCond extends Cond {

    public CmpCond() { }

    public CmpCond(IExpr leftExpr, TokenType operator, IExpr rightExpr) {
        this.elements[0] = leftExpr;
        this.operator = operator;
        this.elements[1] = rightExpr;
    }

    @Override
    public Cond build() throws CompileError {
        this.elements[0] = new Expr(true).build();
        boolean finished = false;
        while (!finished) {
            TokenType type = this.lexer.nextType();
            switch (type) {
                case Lt, Lte, Gt, Gte, Eq, Neq -> {
                    if (this.elements[1] != null) {
                        this.elements[0] = new CmpCond(this.elements[0], this.operator, this.elements[1]);
                    }
                    this.operator = this.lexer.getToken().getType();
                    this.elements[1] = new Expr(true).build();
                }
                default -> finished = true;
            }
        }
        if (elements[1] == null) {
            if (elements[0] instanceof Cond cond) {
                return cond;
            }
            operator = TokenType.Neq;
            elements[1] = new Number(0);
        }
        return this;
    }

    @Override
    public List<Instruction> generateIr() throws CompileError {
        List<Instruction> instructions = new ArrayList<>();
        Operand[] operands = new Operand[2];
        for (int i = 0; i < 2; ++i) {
            IExpr subExpr = elements[i];
            if (subExpr instanceof Cond condition) {
                instructions.addAll(condition.generateIr());
                Operand extended = Operand.local(Tagger.newTag());
                Extend extend = new Extend("i32", extended, "i1", condition.getResult());
                instructions.add(extend);
                operands[i] = extended;
            }
            if (subExpr instanceof Number number) {
                operands[i] = Operand.number(number.getValue());
            }
            else if (subExpr instanceof Ident ident) {
                Symbol symbol = this.table.get(ident, SymbolType.Variable);
                if (symbol instanceof Const constant) {
                    operands[i] = Operand.number(constant.getValue());
                }
                else if (symbol instanceof Variable variable) {
                    operands[i] = Operand.local(Tagger.newTag());
                    Load load = new Load("i32", operands[i], "i32*", variable.getAddress());
                    instructions.add(load);
                }
                else if (symbol instanceof Array array) {
                    throw new SemanticError(ident.getPos(), "expected index for array '" + array.getIdent().getName() + '\'');
                }
            }
            else if (subExpr instanceof Expr expression) {
                instructions.addAll(expression.generateIr());
                if (expression instanceof FuncCall call && expression.getResult() == null) {
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                }
                operands[i] = expression.getResult();
            }
        }
        CmpType cmp = switch (operator) {
            case Lt -> CmpType.Lt;
            case Lte -> CmpType.Lte;
            case Gt -> CmpType.Gt;
            case Gte -> CmpType.Gte;
            case Eq -> CmpType.Eq;
            case Neq -> CmpType.Neq;
            default -> null;
        };
        Operand result = Operand.local(Tagger.newTag());
        Compare compare = new Compare(result, cmp, "i32", operands[0], operands[1]);
        this.result = result;
        instructions.add(compare);
        return instructions;
    }

}
