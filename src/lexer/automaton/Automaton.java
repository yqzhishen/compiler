package lexer.automaton;

import model.token.Token;
import model.token.TokenType;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Automaton {

    private final IState[] states = new IState[] {
            // 0
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (Pattern.matches("\\s", Character.toString(ch)))
                        return 0;
                    if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '_')
                        return 1;
                    if (ch >= '1' && ch <= '9')
                        return 2;
                    if (ch == '0')
                        return 3;
                    if (ch == '/')
                        return 7;
                    return switch (ch) {
                        case '(' -> 13;
                        case ')' -> 14;
                        case '{' -> 15;
                        case '}' -> 16;
                        case ';' -> 17;
                        case '+' -> 18;
                        case '-' -> 19;
                        case '*' -> 20;
                        case '%' -> 21;
                        case '=' -> 22;
                        case ',' -> 24;
                        case '<' -> 25;
                        case '>' -> 27;
                        case '!' -> 29;
                        case '&' -> 31;
                        case '|' -> 33;
                        default -> -1;
                    };
                }

                @Override
                public TokenType terminatorType() {
                    return null;
                }
            },
            // 1
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' || ch == '_')
                        return 1;
                    return -1;
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Ident;
                }
            },
            // 2
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= '0' && ch <= '9')
                        return 2;
                    return -1;
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Dec;
                }
            },
            // 3
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= '0' && ch <= '7')
                        return 4;
                    if (ch == 'x' || ch == 'X')
                        return 5;
                    return -1;
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Oct;
                }
            },
            // 4
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= '0' && ch <= '7')
                        return 4;
                    if (ch == 'x' || ch == 'X')
                        return 5;
                    return -1;
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Oct;
                }
            },
            // 5
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')
                        return 6;
                    return -1;
                }

                @Override
                public TokenType terminatorType() {
                    return null;
                }
            },
            // 6
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')
                        return 6;
                    return -1;
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Hex;
                }
            },
            // 7
            new IState() {
                @Override
                public int recognize(char ch) {
                    return switch (ch) {
                        case '/' -> 8;
                        case '*' -> 9;
                        default -> -1;
                    };
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Div;
                }
            },
            // 8
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch == '\n' || ch == '\r')
                        return 10;
                    return 8;
                }

                @Override
                public TokenType terminatorType() {
                    return TokenType.Comment;
                }
            },
            // 9
            new IState() {
                @Override
                public int recognize(char ch) {
                    if (ch == '*')
                        return 11;
                    return 9;
                }

                @Override
                public TokenType terminatorType() {
                    return null;
                }
            },
            // 10
            new SimpleState(TokenType.Comment),
            // 11
            new IState() {
                @Override
                public int recognize(char ch) {
                    return switch (ch) {
                        case '*' -> 11;
                        case '/' -> 12;
                        default -> 9;
                    };
                }

                @Override
                public TokenType terminatorType() {
                    return null;
                }
            },
            // 12
            new SimpleState(TokenType.Comment),
            // 13
            new SimpleState(TokenType.LPar),
            // 14
            new SimpleState(TokenType.RPar),
            // 15
            new SimpleState(TokenType.LBrace),
            // 16
            new SimpleState(TokenType.RBrace),
            // 17
            new SimpleState(TokenType.Semicolon),
            // 18
            new SimpleState(TokenType.Plus),
            // 19
            new SimpleState(TokenType.Sub),
            // 20
            new SimpleState(TokenType.Mul),
            // 21
            new SimpleState(TokenType.Mod),
            // 22
            new SimpleState(TokenType.Assign, '=', 23),
            // 23
            new SimpleState(TokenType.Eq),
            // 24
            new SimpleState(TokenType.Comma),
            // 25
            new SimpleState(TokenType.Lt, '=', 26),
            // 26
            new SimpleState(TokenType.Lte),
            // 27
            new SimpleState(TokenType.Gt, '=', 28),
            // 28
            new SimpleState(TokenType.Gte),
            // 29
            new SimpleState(TokenType.Not, '=', 30),
            // 30
            new SimpleState(TokenType.Neq),
            // 31
            new SimpleState(null, '&', 32),
            // 32
            new SimpleState(TokenType.And),
            // 33
            new SimpleState(null, '|', 34),
            // 34
            new SimpleState(TokenType.Or)
    };

    private int current = 0;

    private final ArrayList<Character> contents = new ArrayList<>();

    private final ArrayList<Integer> history = new ArrayList<>();

    public Automaton() {
        this.history.add(0);
    }

    public boolean isEmpty() {
        return this.contents.size() == 0;
    }

    public boolean push(char ch) {
        int transfer = this.states[this.current].recognize(ch);
        if (transfer >= 0) {
            if (this.current > 0 || transfer > 0) {
                this.contents.add(ch);
                this.history.add(transfer);
            }
            this.current = transfer;
            return true;
        }
        return false;
    }

    public char pop() {
        this.current = this.history.remove(this.history.size() - 1);
        return this.contents.remove(this.contents.size() - 1);
    }

    public Token getToken() {
        TokenType type = states[this.history.get(this.history.size() - 1)].terminatorType();
        if (type == null)
            return null;
        StringBuilder builder = new StringBuilder();
        contents.forEach(builder::append);
        return new Token(type).filter(builder.toString());
    }

    public void reset() {
        this.contents.clear();
        this.history.clear();
        this.history.add(0);
        this.current = 0;
    }

}
