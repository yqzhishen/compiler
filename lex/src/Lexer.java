import java.io.*;

public class Lexer {

    public static void main(String[] args) throws IOException {
        File src = new File(args[0]);
        FileReader fr = new FileReader(src);
        BufferedReader br = new BufferedReader(fr, 256);
        PushbackReader pr = new PushbackReader(br);
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
                        System.out.println(Lex.reservedToken(token));
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
                        System.out.println(Lex.reservedToken(token));
                        dfa.reset();
                    }
                }
                c = pr.read();
            }
            if (!dfa.isEmpty()) {
                Token token = dfa.getToken();
                if (token == null)
                    throw new ErrException();
                System.out.println(Lex.reservedToken(token));
                dfa.reset();
            }
        }
        catch (ErrException err) {
            System.out.println("Err");
        }
        pr.close();
        br.close();
        fr.close();
    }
}
