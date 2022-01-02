package model.unit;

import error.CompileError;

public interface IMetaUnit extends IUnit {

    String generateCode() throws CompileError;

    boolean isFunction();

}
