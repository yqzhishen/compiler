package model.unit;

import java.util.List;

public interface IArrayInitializer extends IUnit {

    List<IArrayInitializer> initializersOfThisDimension();

}
