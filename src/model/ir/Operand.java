package model.ir;

public class Operand {

    private final boolean tagged;

    private int tag;

    private int value;

    public Operand(boolean tagged, int tagOrValue) {
        this.tagged = tagged;
        if (tagged)
            this.tag = tagOrValue;
        else
            this.value = tagOrValue;
    }

    public boolean isTagged() {
        return tagged;
    }

    public Integer getTag() {
        if (tagged)
            return tag;
        return null;
    }

    public Integer getValue() {
        if (tagged)
            return null;
        return value;
    }

    @Override
    public String toString() {
        return tagged ? "%" + tag : String.valueOf(value);
    }

}
