package model.unit;

import error.CompileError;
import model.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompUnit extends AbstractUnit {

    public static boolean hasMain;

    private final List<IMetaUnit> metaUnits = new ArrayList<>();

    @Override
    public CompUnit build() throws IOException, CompileError {
        while (lexer.nextType() != null) {
            if (TokenType.Const.equals(lexer.nextType())) {
                this.metaUnits.add(new GlobalDef().build());
            }
            else if (TokenType.LPar.equals(lexer.nextType(2))) {
                this.metaUnits.add(new FuncDef().build());
            }
            else {
                this.metaUnits.add(new GlobalDef().build());
            }
        }
        return this;
    }

    public String generateCode() throws CompileError {
        StringBuilder builder = new StringBuilder();
        if (!this.metaUnits.isEmpty()) {
            builder.append(metaUnits.get(0).generateCode());
        }
        for (int i = 1; i < this.metaUnits.size(); ++i) {
            if (metaUnits.get(i - 1).isFunction() || metaUnits.get(i).isFunction()) {
                builder.append('\n');
            }
            builder.append(metaUnits.get(i).generateCode());
        }
        if (!hasMain) {
            throw new CompileError("Missing 'main' function");
        }
        return builder.toString();
    }

}
