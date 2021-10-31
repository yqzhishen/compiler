package analyzer;

import error.SemanticError;
import model.symbol.*;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;
import model.unit.*;

import java.util.List;
import java.util.StringJoiner;

public class SemanticAnalyzer {

    private static final SemanticAnalyzer analyzer = new SemanticAnalyzer();

    public static SemanticAnalyzer getAnalyzer() {
        return analyzer;
    }

    private final SymTable table = SymTable.getSymTable();

    private int tag = 0;

    private final StringJoiner joiner = new StringJoiner("\n    ", "\n    ", "\n");

    private SemanticAnalyzer() { }

    public String dump(CompUnit unit) throws SemanticError {
        StringBuilder builder = new StringBuilder("define dso_local i32 @main() {");
        List<Sentence> sentences = unit
                .getFuncDefs().get(0)
                .getFuncBlock()
                .getSentences();
        for (Sentence sentence : sentences) {
            if (sentence instanceof ConstDecl decl) {
                List<Symbol> symbols = decl.getSymbols();
                for (Symbol symbol : symbols) {
                    Const constant = (Const) symbol;
                    IExpr expr = constant.getExpr();
                    if (expr instanceof Number number) {
                        this.joiner.add("%" + this.tag + " = i32 " + number.getValue());
                        constant.getIdent().setTag(this.tag++);
                        this.table.put(constant);
                    }
                    else if (expr instanceof Ident ident) {
                        Const rConst = (Const) this.table.get(ident, SymbolType.Const);
                        this.joiner.add("%" + this.tag + " = i32 " + "%" + rConst.getIdent().getTag());
                        constant.getIdent().setTag(this.tag++);
                        this.table.put(constant);
                    }
                    else if (expr instanceof FuncCall) {
                        throw new SemanticError(constant.getIdent().getPos(), "not a constant value");
                    }
                    else {
                        dumpConstExpr((Expr) expr);
                        constant.getIdent().setTag(expr.getTag());
                    }
                }
            }
            else if (sentence instanceof VarDecl decl) {
                List<Symbol> symbols = decl.getSymbols();
                for (Symbol symbol : symbols) {
                    Variable variable = (Variable) symbol;
                    this.joiner.add("%" + this.tag + " = alloca i32");
                    symbol.getIdent().setTag(this.tag);
                    this.table.put(variable);
                    ++this.tag;
                    IExpr expr = variable.getExpr();
                    if (expr == null)
                        continue;
                    dumpAssign(symbol.getIdent(), expr);
                }
            }
            else if (sentence instanceof Stmt stmt) {
                dumpAssign(stmt.getLeftVal(), stmt.getExpr());
            }
            else if (sentence instanceof Return ret) {
                IExpr expr = ret.getExpr();
                if (expr instanceof Number number)
                    this.joiner.add("ret i32 " + number.getValue());
                else if (expr instanceof Ident ident) {
                    Symbol symbol = this.table.get(ident, SymbolType.Variable);
                    if (symbol instanceof Const rConst)
                        this.joiner.add("ret i32 %" + rConst.getIdent().getTag());
                    else if (symbol instanceof Variable rVar) {
                        this.joiner.add("%" + this.tag + " = load i32, i32* %" + rVar.getIdent().getTag());
                        this.joiner.add("ret i32 %" + this.tag);
                        ++this.tag;
                    }
                }
                else if (expr instanceof FuncCall call) {
                    Function function = (Function) this.table.get(call.getIdent(), SymbolType.Function);
                    if (function.isVoid())
                        throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                    dumpFuncCall(call);
                    this.joiner.add("ret i32 %" + call.getTag());
                }
                else if (expr instanceof Expr exp) {
                    dumpVarExpr(exp);
                    this.joiner.add("ret i32 %" + exp.getTag());
                }
            }
        }
        builder.append(this.joiner);
        builder.append("}\n");
        return builder.toString();
    }

    public void dumpAssign(Ident leftVal, IExpr expr) throws SemanticError {
        Symbol leftSym;
        Integer leftTag = null;
        if (leftVal != null) {
            leftSym = this.table.get(leftVal, SymbolType.Variable);
            if (leftSym instanceof Const)
                throw new SemanticError(leftVal.getPos(), "constant value '" + leftVal.getName() + "' cannot be assigned");
            leftTag = leftSym.getIdent().getTag();
        }
        if (expr instanceof Number number) {
            if (leftVal != null)
                this.joiner.add("store i32 " + number.getValue() + ", i32* %" + leftTag);
        }
        else if (expr instanceof Ident ident) {
            Symbol symbol = this.table.get(ident, SymbolType.Variable);
            if (leftVal != null) {
                if (symbol instanceof Const rConst)
                    this.joiner.add("store i32 %" + rConst.getIdent().getTag() + ", i32* %" + leftTag);
                else if (symbol instanceof Variable rVar) {
                    this.joiner.add("%" + this.tag + " = load i32, i32* %" + rVar.getIdent().getTag());
                    this.joiner.add("store i32 %" + this.tag + ", i32* %" + leftTag);
                    ++this.tag;
                }
            }
        }
        else if (expr instanceof FuncCall call) {
            Function function = (Function) this.table.get(call.getIdent(), SymbolType.Function);
            if (function.isVoid() && leftVal != null)
                throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
            dumpFuncCall(call);
            if (leftVal != null)
                this.joiner.add("store i32 %" + call.getTag() + ", i32* %" + leftTag);
        }
        else if (expr instanceof Expr exp) {
            dumpVarExpr(exp);
            if (leftVal != null)
                this.joiner.add("store i32 %" + exp.getTag() + ", i32* %" + leftTag);
        }
    }

    public void dumpFuncCall(FuncCall call) throws SemanticError {
        List<IExpr> params = call.getParams();
        Function function = (Function) this.table.get(call.getIdent(), SymbolType.Function);
        if (params.size() < function.getSchema())
            throw new SemanticError(call.getIdent().getPos(), "too few arguments for function '" + call.getIdent().getName() + "'");
        else if (params.size() > function.getSchema())
            throw new SemanticError(call.getIdent().getPos(), "too much arguments for function '" + call.getIdent().getName() + "'");
        StringJoiner callJoiner = new StringJoiner(", ", "(", ")");
        for (IExpr param : params) {
            if (param instanceof Number number)
                callJoiner.add("i32 " + number.getValue());
            else if (param instanceof Ident ident) {
                Symbol pSymbol = this.table.get(ident, SymbolType.Variable);
                if (pSymbol instanceof Const pConst)
                    callJoiner.add("i32 %" + pConst.getIdent().getTag());
                else if (pSymbol instanceof Variable pVar) {
                    this.joiner.add("%" + this.tag + " = load i32, i32* %" + pVar.getIdent().getTag());
                    callJoiner.add("i32 %" + this.tag);
                    ++this.tag;
                }
            }
            else if (param instanceof FuncCall pCall) {
                Function pFunc = (Function) this.table.get(pCall.getIdent(), SymbolType.Function);
                if (pFunc.isVoid())
                    throw new SemanticError(pCall.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                dumpFuncCall(pCall);
                callJoiner.add("i32 %" + pCall.getTag());
            }
            else if (param instanceof Expr pExpr) {
                dumpVarExpr(pExpr);
                callJoiner.add("i32 %" + pExpr.getTag());
            }
        }
        if (function.isVoid())
            this.joiner.add("call void @" + function.getIdent().getName() + callJoiner);
        else {
            this.joiner.add("%" + this.tag + " = call i32 @" + function.getIdent().getName() + callJoiner);
            call.setTag(this.tag++);
        }
    }

    public void dumpConstExpr(Expr expr) throws SemanticError {
        IExpr[] elements = expr.getElements();
        for (IExpr element : elements) {
            if (element instanceof FuncCall funcCall)
                throw new SemanticError(funcCall.getIdent().getPos(), "not a constant value");
            if (element instanceof Expr subExpr)
                dumpConstExpr(subExpr);
        }
        String leftTag = null, rightTag = null;
        if (elements[0] instanceof Number number)
            leftTag = String.valueOf(number.getValue());
        else if (elements[0] instanceof Ident ident) {
            Const constant = (Const) this.table.get(ident, SymbolType.Const);
            leftTag = "%" + constant.getIdent().getTag();
        }
        if (elements[1] instanceof Number number)
            rightTag = String.valueOf(number.getValue());
        else if (elements[1] instanceof Ident ident) {
            Const constant = (Const) this.table.get(ident, SymbolType.Const);
            rightTag = "%" + constant.getIdent().getTag();
        }
        this.joiner.add(String.format("%%%d = %s i32 %s, %s", this.tag, dumpOp(expr.getOperator()), leftTag, rightTag));
        expr.setTag(this.tag++);
    }

    public void dumpVarExpr(Expr expr) throws SemanticError {
        IExpr[] elements = expr.getElements();
        String[] labels = new String[2];
        for (int i = 0; i < 2; ++i) {
            IExpr subExpr = elements[i];
            if (subExpr instanceof Number number)
                labels[i] = String.valueOf(number.getValue());
            else if (subExpr instanceof Ident ident) {
                Symbol symbol = this.table.get(ident, SymbolType.Variable);
                if (symbol instanceof Const constant)
                    labels[i] = "%" + constant.getIdent().getTag();
                else if (symbol instanceof Variable variable) {
                    this.joiner.add("%" + this.tag + " = load i32, i32* %" + variable.getIdent().getTag());
                    labels[i] = "%" + this.tag;
                    ++this.tag;
                }
            }
            else if (subExpr instanceof FuncCall call) {
                Function function = (Function) this.table.get(call.getIdent(), SymbolType.Function);
                if (function.isVoid())
                    throw new SemanticError(call.getIdent().getPos(), "incompatible type (required 'int', got 'void'");
                dumpFuncCall(call);
                labels[i] = "%" + call.getTag();
            }
            else if (subExpr instanceof Expr expression) {
                dumpVarExpr(expression);
                labels[i] = "%" + expression.getTag();
            }
        }
        this.joiner.add(String.format("%%%d = %s i32 %s, %s", this.tag, dumpOp(expr.getOperator()), labels[0], labels[1]));
        expr.setTag(this.tag);
        ++this.tag;
    }

    private String dumpOp(TokenType op) {
        return switch (op) {
            case Plus -> "add";
            case Sub -> "sub";
            case Mul -> "mul";
            case Div -> "sdiv";
            case Mod -> "srem";
            default -> null;
        };
    }

}
