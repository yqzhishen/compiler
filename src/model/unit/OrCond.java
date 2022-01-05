package model.unit;

import error.CompileError;
import model.token.TokenType;

public class OrCond extends Cond {

    @Override
    public IExpr build() throws CompileError {
        this.elements[0] = new AndCond().build();
        boolean finished = false;
        while (!finished) {
            TokenType type = this.lexer.nextType();
            if (TokenType.Or.equals(type)) {
                if (this.elements[1] != null) {
                    this.elements[0] = new Cond(this.elements[0], this.operator, this.elements[1]);
                }
                this.operator = this.lexer.getToken().getType();
                this.elements[1] = new AndCond().build();
            }
            else {
                finished = true;
            }
        }
        return this.elements[1] == null ? this.elements[0] : this;
    }

}
