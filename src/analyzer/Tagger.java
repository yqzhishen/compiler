package analyzer;

public class Tagger {

    private static int tag = 0;

    public static void reset() {
        tag = 0;
    }

    public static int newTag() {
        return tag++;
    }

    private Tagger() { }

}
