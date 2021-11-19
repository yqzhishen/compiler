package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class CmpCond extends Cond {

    @Override
    public IExpr build() throws CompileError, IOException {
        this.elements[0] = new Expr().build();
        boolean finished = false;
        while (!finished) {
            TokenType type = this.lexer.nextType();
            switch (type) {
                case Lt, Lte, Gt, Gte, Eq, Neq -> {
                    if (this.elements[1] != null) {
                        this.elements[0] = new Cond(this.elements[0], this.operator, this.elements[1]);
                    }
                    this.operator = this.lexer.getToken().getType();
                    this.elements[1] = new Expr().build();
                }
                default -> finished = true;
            }
        }
        return this.elements[1] == null ? this.elements[0] : this;
    }

}
