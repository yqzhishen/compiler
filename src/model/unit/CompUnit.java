package model.unit;

import error.CompileError;
import model.ir.Instruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

}
