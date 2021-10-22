package model.unit;

import error.CompileError;

import java.io.IOException;

public class CompUnit extends AbstractUnit {

    private FuncDef funcDef;

    @Override
    public CompUnit build() throws IOException, CompileError {
        this.funcDef = new FuncDef().build();
        return this;
    }

    @Override
    public String dump() {
        return this.funcDef.dump() + '\n';
    }

}
