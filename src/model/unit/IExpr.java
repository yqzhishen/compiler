package model.unit;

import error.CompileError;

public interface IExpr extends IUnit {

    Integer calculate() throws CompileError;

    default void setTag(int tag) { }

    default Integer getTag() {
        return null;
    }

}
