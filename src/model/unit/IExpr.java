package model.unit;

import error.CompileError;

public interface IExpr extends IUnit {

    Integer calculate() throws CompileError;

}
