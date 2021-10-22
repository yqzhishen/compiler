package model.unit;

import error.CompileError;

import java.io.IOException;
import java.util.List;

public interface IUnit {

    boolean isTerminator();

    List<IUnit> subUnits();

    default IUnit build() throws IOException, CompileError {
        return null;
    }

    String dump();

}
