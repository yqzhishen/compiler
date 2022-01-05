package model.unit;

import error.CompileError;

import java.io.IOException;

public interface IUnit {

    IUnit build() throws CompileError;

}
