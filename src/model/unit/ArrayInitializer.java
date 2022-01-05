package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class ArrayInitializer extends AbstractUnit implements IArrayInitializer {

    public static final ArrayInitializer ZERO_INITIALIZER = new ArrayInitializer(-1);

    public static ArrayInitializer global(int dimension) {
        return new ArrayInitializer(true, dimension);
    }

    public static ArrayInitializer local(int dimension) {
        return new ArrayInitializer(false, dimension);
    }

    private boolean global;

    private final int dimension;

    private final List<IArrayInitializer> initializers = new ArrayList<>();

    private ArrayInitializer(int dimension) {
        this.dimension = dimension;
    }

    private ArrayInitializer(boolean global, int dimension) {
        this.global = global;
        this.dimension = dimension;
    }

    @Override
    public IArrayInitializer build() throws CompileError {
        if (dimension == 0) {
            return new Expr().build();
        }
        this.require(TokenType.LBrace);
        if (TokenType.RBrace.equals(lexer.nextType())) {
            lexer.getToken();
            if (global)
                return ZERO_INITIALIZER;
            return this;
        }
        this.initializers.add(new ArrayInitializer(global, dimension - 1).build());
        while (!TokenType.RBrace.equals(lexer.nextType())) {
            this.require(TokenType.Comma);
            this.initializers.add(new ArrayInitializer(global,dimension - 1).build());
        }
        this.require(TokenType.RBrace);
        return this;
    }

    @Override
    public List<IArrayInitializer> initializersOfThisDimension() {
        return initializers;
    }
}
