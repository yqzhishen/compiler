package model.token;

public enum TokenType {

    Main("@main"), Int("i32"), Return("return"),
    LPar("("), RPar(")"),
    LBrace("{"), RBrace("}"),
    Semicolon(";"), Ident, Number, Dec, Oct, Hex, Comment;


    private final String dumpString;

    TokenType() {
        this.dumpString = null;
    }

    TokenType(String dumpString) {
        this.dumpString = dumpString;
    }

    public String dump() {
        return this.dumpString;
    }

}
