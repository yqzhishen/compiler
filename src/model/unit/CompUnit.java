package model.unit;

import error.LexicalError;
import error.SyntaxError;
import lexer.Lexer;
import model.token.TokenType;

import java.io.IOException;

public class CompUnit extends AbstractUnit {

    public CompUnit(Lexer lexer) {
        super(lexer);
    }

    @Override
    public IUnit build() throws LexicalError, SyntaxError, IOException {
        this.append(TokenType.Int);
        this.append(TokenType.Main);
        this.append(TokenType.LPar);
        this.append(TokenType.RPar);
        this.subUnits.add(new Block(this.lexer).build());
        return this;
    }

    @Override
    public String dump() {
        return "define dso_local i32 @main() " + this.subUnits.get(4).dump() + '\n';
    }

}
