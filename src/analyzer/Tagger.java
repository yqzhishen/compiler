package analyzer;

public class Tagger {

    private static int tag;

    public static int newTag() {
        return ++tag;
    }

    private Tagger() { }

}
