package model.unit;

import error.LexicalError;
import error.SyntaxError;

import java.io.IOException;
import java.util.List;

public interface IUnit {

    boolean isTerminator();

    List<IUnit> subUnits();

    default IUnit build() throws LexicalError, SyntaxError, IOException {
        return null;
    }

    String dump();

}
