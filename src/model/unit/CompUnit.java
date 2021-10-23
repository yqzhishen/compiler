package model.unit;

import error.CompileError;

import java.io.IOException;
import java.util.ArrayList;

public class CompUnit extends AbstractUnit {

    private final ArrayList<FuncDef> funcDefs = new ArrayList<>();

    public ArrayList<FuncDef> getFuncDefs() {
        return this.funcDefs;
    }

    @Override
    public CompUnit build() throws IOException, CompileError {
        this.funcDefs.add(new FuncDef().build());
        return this;
    }

    @Override
    public String dump() {
        return this.funcDefs.get(0).dump() + '\n';
    }

}
