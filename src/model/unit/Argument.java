package model.unit;

import error.CompileError;
import model.ir.Operand;
import model.token.Ident;
import model.token.Number;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Argument extends AbstractUnit {

    private Ident ident;

    private boolean isPointer;

    private final List<IExpr> shape = new ArrayList<>();

    private Operand address;

    public Argument() {}

    public Argument(boolean isPointer, int ... shape) {
        this.isPointer = isPointer;
        for (int i : shape) {
            this.shape.add(new Number(i));
        }
    }

    public Ident getIdent() {
        return ident;
    }

    public boolean isPointer() {
        return isPointer;
    }

    public List<IExpr> getShape() {
        return shape;
    }

    public void setAddress(Operand address) {
        this.address = address;
    }

    public Operand getAddress() {
        return address;
    }

    @Override
    public Argument build() throws IOException, CompileError {
        this.require(TokenType.Int);
        ident = (Ident) this.require(TokenType.Ident);
        isPointer = TokenType.LBracket.equals(lexer.nextType());
        if (isPointer) {
            lexer.getToken(); // LBracket
            this.require(TokenType.RBracket);
            while (TokenType.LBracket.equals(lexer.nextType())) {
                this.require(TokenType.LBracket);
                shape.add(new Expr().build());
                this.require(TokenType.RBracket);
            }
        }
        return this;
    }

}
