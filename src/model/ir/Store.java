package model.ir;

public class Store extends Instruction {

    private final String storedType;

    private final Operand stored;

    private final String addressType;

    private final Operand address;

    public Store(String storedType, Operand stored, String addressType, Operand address) {
        this.storedType = storedType;
        this.stored = stored;
        this.addressType = addressType;
        this.address = address;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.Store;
    }

    @Override
    public String toString() {
        return "store " + storedType + " " + stored + ", " + addressType + " " + address;
    }
}
