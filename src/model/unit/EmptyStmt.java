package model.unit;

import model.ir.Instruction;

import java.util.Collections;
import java.util.List;

public class EmptyStmt extends Sentence {

    @Override
    public EmptyStmt build() {
        throw new UnsupportedOperationException("Empty statements cannot be built");
    }

    @Override
    public List<Instruction> generateIr() {
        return Collections.emptyList();
    }

}
