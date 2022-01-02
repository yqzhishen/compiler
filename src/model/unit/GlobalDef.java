package model.unit;

import analyzer.SymTable;
import error.CompileError;
import error.SemanticError;
import model.ir.Operand;
import model.symbol.Array;
import model.symbol.Const;
import model.symbol.Symbol;
import model.symbol.Variable;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class GlobalDef extends AbstractUnit implements IMetaUnit {

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
        this.symbols.add(this.buildInitializer());
        while (!TokenType.Semicolon.equals(this.lexer.nextType())) {
            this.require(TokenType.Comma);
            this.symbols.add(this.buildInitializer());
        }
        this.require(TokenType.Semicolon);
        return this;
    }

    private Symbol buildInitializer() throws IOException, CompileError {
        Ident ident = (Ident) this.require(TokenType.Ident);
        boolean isArray = TokenType.LBracket.equals(lexer.nextType());
        IExpr expr = null;
        List<IExpr> shape = null;
        ArrayInitializer initializer = null;
        if (isArray) {
            shape = new ArrayList<>();
            initializer = ArrayInitializer.ZERO_INITIALIZER;
            do {
                lexer.getToken();
                shape.add(new Expr().build());
                this.require(TokenType.RBracket);
            } while (TokenType.LBracket.equals(lexer.nextType()));
        }
        else {
            expr = new Number(0);
        }
        if (TokenType.Assign.equals(this.lexer.nextType())) {
            this.lexer.getToken();
            if (isArray) {
                initializer = (ArrayInitializer) ArrayInitializer.global(shape.size()).build();
            }
            else {
                expr = new Expr().build();
            }
        }
        if (isArray) {
            if (this.isConst) {
                return Array.constArray(ident, shape, initializer);
            }
            else {
                return Array.varArray(ident, shape, initializer);
            }
        }
        else {
            return this.isConst ? new Const(ident, expr) : new Variable(ident, expr);
        }
    }

    @Override
    public String generateCode() throws CompileError {
        SymTable table = SymTable.getInstance();
        StringBuilder builder = new StringBuilder();
        if (this.isConst) {
            for (Symbol symbol : this.symbols) {
                if (symbol instanceof Const constant) {
                    constant.setValue(constant.getExpr().calculate());
                    table.put(constant);
                }
                else if (symbol instanceof Array array) {
                    builder.append(dumpArray(array, "constant"));
                }
            }
        }
        else {
            for (Symbol symbol : this.symbols) {
                if (symbol instanceof Variable variable) {
                    String name = variable.getIdent().getName();
                    variable.setAddress(Operand.global(name));
                    table.put(variable);
                    builder.append('@').append(name).append(" = dso_local global i32 ");
                    builder.append(variable.getExpr().calculate()).append('\n');
                }
                else if (symbol instanceof Array array) {
                    builder.append(dumpArray(array, "global"));
                }
            }
        }
        return builder.toString();
    }

    private String dumpArray(Array array, String tag) throws CompileError {
        List<IExpr> shape = array.getShape();
        for (int i = 0; i < shape.size(); ++i) {
            int size = shape.get(i).calculate();
            if (size < 0)
                throw new SemanticError(array.getIdent().getPos(), "array size must not be negative");
            shape.set(i, new Number(size));
        }
        ArrayInitializer initializer = array.getInitializer();
        broadcastInitializer(shape, initializer);
        String name = array.getIdent().getName();
        array.setAddress(Operand.global(name));
        SymTable.getInstance().put(array);
        try {
            return String.format("@%s = dso_local %s %s\n", name, tag, dumpInitializer(shape, initializer));
        }
        catch (IllegalStateException e) {
            throw new SemanticError(array.getIdent().getPos(), "initializer index out of bound");
        }
    }

    private void broadcastInitializer(List<IExpr> shape, IArrayInitializer initializer) {
        List<IArrayInitializer> initializers = initializer.initializersOfThisDimension();
        int length = ((Number) shape.get(0)).getValue();
        if (shape.size() == 1) {
            for (int i = initializers.size(); i < length; ++i) {
                initializers.add(new Number(0));
            }
        }
        else {
            for (int i = 0; i < length && i < initializers.size(); ++i) {
                broadcastInitializer(shape.subList(1, shape.size()), initializers.get(i));
            }
            for (int i = initializers.size(); i < length; ++i) {
                initializers.add(ArrayInitializer.ZERO_INITIALIZER);
            }
        }
    }

    private String dumpInitializer(List<IExpr> shape, IArrayInitializer initializer) throws CompileError {
        StringBuilder builder = new StringBuilder();
        builder.append(Array.dumpShape(shape)).append(' ');
        if (shape.size() == 0) {
            builder.append(((IExpr) initializer).calculate());
            return builder.toString();
        }
        if (initializer.equals(ArrayInitializer.ZERO_INITIALIZER)) {
            builder.append("zeroinitializer");
            return builder.toString();
        }
        List<IArrayInitializer> initializers = initializer.initializersOfThisDimension();
        if (initializers.size() > ((Number) shape.get(0)).getValue()) {
            throw new IllegalStateException();
        }
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (IArrayInitializer subInitializer : initializers) {
            joiner.add(dumpInitializer(shape.subList(1, shape.size()), subInitializer));
        }
        builder.append(joiner);
        return builder.toString();
    }

    @Override
    public boolean isFunction() {
        return false;
    }

}
