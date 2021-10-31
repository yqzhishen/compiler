package model.unit;

public interface IExpr extends IUnit {

    Integer calculate();

    default void setTag(int tag) { }

    default Integer getTag() {
        return null;
    }

}
