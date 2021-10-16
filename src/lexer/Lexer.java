package lexer;

import lexer.automaton.Automaton;
import error.LexicalError;
import model.token.Token;
import model.token.TokenType;

import java.io.*;
import java.util.Stack;

public class Lexer {

    private final PushbackReader reader;

    private final Automaton automaton = new Automaton();

    private final Stack<Token> buffer = new Stack<>();

    public Lexer(PushbackReader reader) {
        this.reader = reader;
    }

    public Token readToken() throws LexicalError, IOException {
        if (!this.buffer.isEmpty())
            return this.buffer.pop();
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
                    throw new LexicalError();
                if (token != null) {
                    this.automaton.reset();
                    if (!token.getType().equals(TokenType.Comment))
                        return token;
                }
            }
            c = this.reader.read();
        }
        if (!this.automaton.isEmpty()) {
            Token token = this.automaton.getToken();
            if (token == null)
                throw new LexicalError();
            this.automaton.reset();
            if (!token.getType().equals(TokenType.Comment))
                return token;
        }
        return null;
    }

    public void unreadToken(Token token) {
        this.buffer.push(token);
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
        catch (LexicalError err) {
            System.out.println("Err");
        }
        pr.close();
        br.close();
        fr.close();
    }

}
