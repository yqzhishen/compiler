package model.unit;

import error.CompileError;

import java.io.IOException;

public interface IUnit {

    boolean isTerminator();

    default IUnit build() throws IOException, CompileError {
        return null;
    }

    String dump();

}
