package model.unit;

import error.CompileError;
import model.token.TokenType;

public class MulExpr extends Expr {

    @Override
    public IExpr build() throws CompileError {
        this.elements[0] = new Term().build();
        boolean finished = false;
        while (!finished) {
            TokenType type = this.lexer.nextType();
            switch (type) {
                case Mul, Div, Mod -> {
                    if (this.elements[1] != null) {
                        this.elements[0] = new Expr(this.elements[0], this.operator, this.elements[1]);
                    }
                    this.operator = this.lexer.getToken().getType();
                    this.elements[1] = new Term().build();
                }
                default -> finished = true;
            }
        }
        return this.elements[1] == null ? this.elements[0] : this;
    }

}
