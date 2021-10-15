package lexer.automaton;

import model.token.TokenType;

public interface IState {

    int recognize(char ch);

    TokenType terminatorType();

}
