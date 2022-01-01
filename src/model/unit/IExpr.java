package model.unit;

import error.CompileError;

import java.util.List;

public interface IExpr extends IUnit, IArrayInitializer {

    Integer calculate() throws CompileError;

    @Override
    default List<IArrayInitializer> initializersOfThisDimension() {
        throw new IllegalStateException("No initializer for 0-dimension value");
    }

}
