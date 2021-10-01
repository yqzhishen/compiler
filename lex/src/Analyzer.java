import java.io.*;

public class Analyzer {

    public static Token fixToken(Token original) {
        TokenType type = original.getType();
        if (type.equals(TokenType.Ident)) {
            String name = original.getParam();
            return switch (name) {
                case "if" -> new Token(TokenType.If);
                case "else" -> new Token(TokenType.Else);
                case "while" -> new Token(TokenType.While);
                case "break" -> new Token(TokenType.Break);
                case "continue" -> new Token(TokenType.Continue);
                case "return" -> new Token(TokenType.Return);
                default -> original;
            };
        }
        return original;
    }

    public static void main(String[] args) throws IOException {
        File src = new File(args[0]);
        FileReader fr = new FileReader(src);
        PushbackReader pr = new PushbackReader(fr);
        Automaton dfa = new Automaton(Lex.states);
        try {
            int c = pr.read();
            while (c != -1) {
                char ch = (char) c;
                if (Character.isWhitespace(ch)) {
                    if (!dfa.isEmpty()) {
                        Token token = dfa.getToken();
                        if (token == null)
                            throw new ErrException();
                        System.out.println(fixToken(token));
                        dfa.reset();
                    }
                }
                else if (!dfa.push(ch)) {
                    pr.unread(c);
                    Token token = null;
                    while (!dfa.isEmpty()) {
                        token = dfa.getToken();
                        if (token != null)
                            break;
                        pr.unread(dfa.pop());
                    }
                    if (dfa.isEmpty())
                        throw new ErrException();
                    if (token != null) {
                        System.out.println(fixToken(token));
                        dfa.reset();
                    }
                }
                c = pr.read();
            }
            if (!dfa.isEmpty()) {
                Token token = dfa.getToken();
                if (token == null)
                    throw new ErrException();
                System.out.println(fixToken(token));
                dfa.reset();
            }
        }
        catch (ErrException err) {
            System.out.println("Err");
        }
    }
}
