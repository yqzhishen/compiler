package model.unit;

import error.CompileError;
import error.SyntaxError;
import model.ir.Instruction;
import model.token.Ident;
import model.token.TokenType;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

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
        Ident ident = (Ident) this.require(TokenType.Ident);
        if (!ident.getName().equals("main")) {
            throw new SyntaxError(ident.getPos(), new TokenType[]{ TokenType.Main }, TokenType.Ident);
        }
        this.funcName = ident.getName();
        this.require(TokenType.LPar);
        this.require(TokenType.RPar);
        this.funcBlock = new Block().build();
        return this;
    }

    public String generateCode() throws CompileError {
        StringBuilder builder = new StringBuilder("define dso_local i32 @").append(funcName).append("() {");
        StringJoiner joiner = new StringJoiner("\n    ", "\n    ", "\n");
        List<Instruction> instructions = funcBlock.generateIr();
        instructions.forEach(instruction -> joiner.add(instruction.toString()));
        builder.append(joiner).append("}\n");
        return builder.toString();
    }

}
