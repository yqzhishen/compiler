package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompUnit extends AbstractUnit {

    private final List<GlobalDef> globalDefs = new ArrayList<>();

    private final List<FuncDef> funcDefs = new ArrayList<>();

    public List<FuncDef> getFuncDefs() {
        return this.funcDefs;
    }

    @Override
    public CompUnit build() throws IOException, CompileError {
        while (true) {
            if (TokenType.Const.equals(lexer.nextType())) {
                this.globalDefs.add(new GlobalDef().build());
            }
            if (TokenType.LPar.equals(lexer.nextType(2)))
                break;
            this.globalDefs.add(new GlobalDef().build());
        }
        this.funcDefs.add(new FuncDef().build());
        return this;
    }

    public String generateCode() throws CompileError {
        StringBuilder builder = new StringBuilder();
        for (GlobalDef globalDef : this.globalDefs) {
            builder.append(globalDef.generateCode());
        }
        for (FuncDef funcDef : this.funcDefs) {
            builder.append(funcDef.generateCode());
        }
        return builder.toString();
    }

}
