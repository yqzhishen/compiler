import java.util.ArrayList;

public class Automaton {

    private final ArrayList<Character> contents = new ArrayList<>();

    private final IState[] states;

    private final ArrayList<Integer> history = new ArrayList<>();

    public Automaton(IState[] states) {
        this.states = states;
        this.history.add(0);
    }

    public boolean isEmpty() {
        return this.contents.size() == 0;
    }

    public boolean push(char ch) {
        int transfer = this.states[this.history.get(this.history.size() - 1)].recognize(ch);
        if (transfer >= 0) {
            this.contents.add(ch);
            this.history.add(transfer);
            return true;
        }
        return false;
    }

    public char pop() {
        this.history.remove(this.history.size() - 1);
        return this.contents.remove(this.contents.size() - 1);
    }

    public Token getToken() {
        TokenType type = states[this.history.get(this.history.size() - 1)].endTokenType();
        if (type == null)
            return null;
        else if (type.equals(TokenType.Ident) || type.equals(TokenType.Number)) {
            StringBuilder builder = new StringBuilder();
            contents.forEach(builder::append);
            return new Token(type, builder.toString());
        }
        else {
            return new Token(type);
        }
    }

    public void reset() {
        this.contents.clear();
        this.history.clear();
        this.history.add(0);
    }

}
