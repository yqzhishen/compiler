package lexer;

import error.LexicalError;
import lexer.automaton.Automaton;
import model.token.Token;
import model.token.TokenType;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Lexer {

    private static PushbackReader reader;

    private static final Lexer lexer = new Lexer();

    public static void setReader(PushbackReader reader) {
        Lexer.reader = reader;
    }

    public static Lexer getLexer() {
        return lexer;
    }

    private Lexer() { }

    private final Automaton automaton = new Automaton();

    private final Queue<Token> buffer = new LinkedList<>();

    public Token readToken() throws LexicalError, IOException {
        if (!this.buffer.isEmpty())
            return this.buffer.poll();
        int c = reader.read();
        while (c != -1) {
            char ch = (char) c;
            if (!this.automaton.push(ch)) {
                reader.unread(c);
                Token token = null;
                while (!this.automaton.isEmpty()) {
                    token = this.automaton.getToken();
                    if (token != null)
                        break;
                    reader.unread(this.automaton.pop());
                }
                if (this.automaton.isEmpty())
                    throw new LexicalError();
                if (token != null) {
                    this.automaton.reset();
                    if (!token.getType().equals(TokenType.Comment))
                        return token;
                }
            }
            c = reader.read();
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

    public Token nextToken() throws LexicalError, IOException {
        if (this.buffer.isEmpty()) {
            Token token = this.readToken();
            if (token != null)
                this.buffer.offer(token);
            return token;
        }
        return this.buffer.peek();
    }

}
