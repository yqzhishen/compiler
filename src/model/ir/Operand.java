package model.ir;

public class Operand {

    private boolean tagged;

    private int tag;

    private int value;

    private String name;

    public static Operand reg(int tag) {
        return new Operand(true, tag);
    }

    public static Operand number(int value) {
        return new Operand(false, value);
    }

    public static Operand global(String name) {
        return new Operand(name);
    }

    public Operand(String globalName) {
        this.name = globalName;
    }

    public Operand(boolean tagged, int tagOrValue) {
        this.tagged = tagged;
        if (tagged)
            this.tag = tagOrValue;
        else
            this.value = tagOrValue;
    }

    public Integer getValue() {
        if (tagged)
            return null;
        return value;
    }

    @Override
    public String toString() {
        if (name == null)
            return tagged ? "%" + tag : String.valueOf(value);
        return "@" + name;
    }

}
