public interface IState {

    int recognize(char ch);

    TokenType endTokenType();

}
