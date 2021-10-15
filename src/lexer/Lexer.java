package lexer;

import lexer.automaton.Automaton;
import error.LexError;
import model.token.Token;

import java.io.*;

public class Lexer {

    private final PushbackReader reader;

    private final Automaton automaton = new Automaton();

    public Lexer(PushbackReader reader) {
        this.reader = reader;
    }

    public Token readToken() throws LexError, IOException {
        int c = reader.read();
        while (c != -1) {
            char ch = (char) c;
            if (!this.automaton.push(ch)) {
                this.reader.unread(c);
                Token token = null;
                while (!this.automaton.isEmpty()) {
                    token = this.automaton.getToken();
                    if (token != null)
                        break;
                    this.reader.unread(this.automaton.pop());
                }
                if (this.automaton.isEmpty())
                    throw new LexError();
                if (token != null) {
                    this.automaton.reset();
                    return token;
                }
            }
            c = this.reader.read();
        }
        if (!this.automaton.isEmpty()) {
            Token token = this.automaton.getToken();
            if (token == null)
                throw new LexError();
            this.automaton.reset();
            return token;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        File src = new File(args[0]);
        FileReader fr = new FileReader(src);
        BufferedReader br = new BufferedReader(fr, 256);
        PushbackReader pr = new PushbackReader(br);
        Lexer lexer = new Lexer(pr);
        try {
            Token token = lexer.readToken();
            while (token != null) {
                System.out.println(token);
                token = lexer.readToken();
            }
        }
        catch (LexError err) {
            System.out.println("Err");
        }
        pr.close();
        br.close();
        fr.close();
    }

}
