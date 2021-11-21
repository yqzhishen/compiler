package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;

public class FuncDef extends AbstractUnit {

    private TokenType funcType; // Only "int" for now

    private String funcName; // Only "main" for now

    private Block funcBlock;

    public Block getFuncBlock() {
        return this.funcBlock;
    }

    @Override
    public FuncDef build() throws IOException, CompileError {
        this.require(TokenType.Int);
        this.require(TokenType.Main);
        this.require(TokenType.LPar);
        this.require(TokenType.RPar);
        this.funcBlock = new Block().build();
        return this;
    }

}
