package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CompUnit extends AbstractUnit {

    public static boolean hasMain;

    private final List<GlobalDef> globalDefs = new ArrayList<>();

    private final List<FuncDef> funcDefs = new ArrayList<>();

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
        while (lexer.nextType() != null) {
            this.funcDefs.add(new FuncDef().build());
        }
        return this;
    }

    public String generateCode() throws CompileError {
        StringBuilder builder = new StringBuilder();
        for (GlobalDef globalDef : this.globalDefs) {
            builder.append(globalDef.generateCode());
        }
        if (!globalDefs.isEmpty()) {
            builder.append('\n');
        }
        StringJoiner funcJoiner = new StringJoiner("\n");
        for (FuncDef funcDef : this.funcDefs) {
            funcJoiner.add(funcDef.generateCode());
        }
        builder.append(funcJoiner);
        if (!hasMain) {
            throw new CompileError("Missing 'main' function");
        }
        return builder.toString();
    }

}
