package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class AndCond extends Cond {

    @Override
    public IExpr build() throws CompileError, IOException {
        this.elements[0] = new CmpCond().build();
        boolean finished = false;
        while (!finished) {
            TokenType type = this.lexer.nextType();
            if (TokenType.And.equals(type)) {
                if (this.elements[1] != null) {
                    this.elements[0] = new Cond(this.elements[0], this.operator, this.elements[1]);
                }
                this.operator = this.lexer.getToken().getType();
                this.elements[1] = new CmpCond().build();
            }
            else {
                finished = true;
            }
        }
        return this.elements[1] == null ? this.elements[0] : this;
    }

}
