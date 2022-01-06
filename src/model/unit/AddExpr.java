package model.unit;

import error.CompileError;
import model.token.TokenType;

public class AddExpr extends Expr {

    public AddExpr(boolean bool) {
        this.bool = bool;
    }

    @Override
    public IExpr build() throws CompileError {
        this.elements[0] = new MulExpr(bool).build();
        boolean finished = false;
        while (!finished) {
            TokenType type = this.lexer.nextType();
            switch (type) {
                case Add, Sub -> {
                    if (this.elements[1] != null) {
                        this.elements[0] = new Expr(this.elements[0], this.operator, this.elements[1]);
                    }
                    this.operator = this.lexer.getToken().getType();
                    this.elements[1] = new MulExpr(bool).build();
                }
                default -> finished = true;
            }
        }
        return this.elements[1] == null ? this.elements[0] : this;
    }

}
