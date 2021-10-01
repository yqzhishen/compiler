public class Lex {

    public static final IState[] states = new IState[] {
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '_')
                        return 1;
                    if (Character.isDigit(ch))
                        return 2;
                    return switch (ch) {
                        case '=' -> 3;
                        case ';' -> 5;
                        case '(' -> 6;
                        case ')' -> 7;
                        case '{' -> 8;
                        case '}' -> 9;
                        case '+' -> 10;
                        case '*' -> 11;
                        case '/' -> 12;
                        case '<' -> 13;
                        case '>' -> 14;
                        default -> -1;
                    };
                }

                @Override
                public TokenType getTokenType() {
                    return null;
                }
            },
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '_' || Character.isDigit(ch))
                        return 1;
                    return -1;
                }

                @Override
                public TokenType getTokenType() {
                    return TokenType.Ident;
                }
            },
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (Character.isDigit(ch))
                        return 2;
                    return -1;
                }

                @Override
                public TokenType getTokenType() {
                    return TokenType.Number;
                }
            },
            new SimpleState('=', 4, TokenType.Assign),
            new SimpleState(TokenType.Eq),
            new SimpleState(TokenType.Semicolon),
            new SimpleState(TokenType.LPar),
            new SimpleState(TokenType.RPar),
            new SimpleState(TokenType.LBrace),
            new SimpleState(TokenType.RBrace),
            new SimpleState(TokenType.Plus),
            new SimpleState(TokenType.Mult),
            new SimpleState(TokenType.Div),
            new SimpleState(TokenType.Lt),
            new SimpleState(TokenType.Gt)
    };

}
