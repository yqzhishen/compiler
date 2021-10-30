package lexer;

import error.LexicalError;
import lexer.automaton.Automaton;
import model.token.Token;
import model.token.TokenType;
import reader.CompileReader;
import reader.FilePosition;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Lexer {

    private static CompileReader reader;

    private static final Lexer lexer = new Lexer();

    public static void setReader(CompileReader reader) {
        Lexer.reader = reader;
    }

    public static CompileReader getReader() {
        return reader;
    }

    public static Lexer getLexer() {
        return lexer;
    }

    private Lexer() { }

    private final Automaton automaton = new Automaton();

    private final Queue<Token> buffer = new LinkedList<>();

    private FilePosition pos = new FilePosition();

    private final LexicalError error = new LexicalError();

    public Token readToken() throws LexicalError, IOException {
        if (!this.buffer.isEmpty())
            return this.buffer.poll();
        int c = reader.read();
        while (c != -1) {
            if (this.automaton.isEmpty())
                this.pos = reader.getPos().copy();
            char ch = (char) c;
            if (!this.automaton.push(ch)) {
                this.error.setParam(reader.getPos().copy(), ch);
                reader.unread(c);
                Token token = null;
                while (!this.automaton.isEmpty()) {
                    token = this.automaton.getToken();
                    if (token != null)
                        break;
                    reader.unread(this.automaton.pop());
                }
                if (this.automaton.isEmpty())
                    throw this.error;
                if (token != null) {
                    this.automaton.reset();
                    if (!token.getType().equals(TokenType.Comment))
                        return token.setPos(this.pos);
                }
            }
            c = reader.read();
        }
        if (!this.automaton.isEmpty()) {
            Token token = this.automaton.getToken();
            if (token == null) {
                this.error.setParam(reader.getPos().copy(), -1);
                throw this.error;
            }
            this.automaton.reset();
            if (!token.getType().equals(TokenType.Comment))
                return token.setPos(this.pos);
        }
        return null;
    }

    public TokenType nextType() throws LexicalError, IOException {
        if (this.buffer.isEmpty()) {
            Token token = this.readToken();
            if (token == null)
                return null;
            this.buffer.offer(token);
            return token.getType();
        }
        return this.buffer.peek().getType();
    }

    public static void main(String[] args) throws IOException, LexicalError {
        CompileReader reader = new CompileReader("test/input.sy");
        Lexer.setReader(reader);
        Lexer lexer = Lexer.getLexer();
        Token token = lexer.readToken();
        while (token != null) {
            System.out.println(token);
            token = lexer.readToken();
        }
    }

}
