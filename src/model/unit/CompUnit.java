package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class CompUnit extends AbstractUnit {

    @Override
    public IUnit build() throws IOException, CompileError {
        this.require(TokenType.Int);
        this.require(TokenType.Main);
        this.require(TokenType.LPar);
        this.require(TokenType.RPar);
        this.subUnits.add(new Block().build());
        return this;
    }

    @Override
    public String dump() {
        return "define dso_local i32 @main() " + this.subUnits.get(4).dump() + '\n';
    }

}
