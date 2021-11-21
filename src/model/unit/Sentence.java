package model.unit;

import analyzer.SymTable;
import error.CompileError;
import model.ir.Instruction;

import java.io.IOException;
import java.util.List;

public abstract class Sentence extends AbstractUnit {

    protected SymTable table = SymTable.getSymTable();

    @Override
    public abstract Sentence build() throws IOException, CompileError;

    public abstract List<Instruction> dump() throws CompileError;

}
