public class Literal implements ParseTree {
    private final Lexeme literal;

    Literal(Lexeme literal) {
        this.literal = literal;
    }

    public EvalResult eval(final RefEnv env) {
        final EvalResult result = new EvalResult();

        if (this.literal.tokenType() == TokenType.NUMBER) {
            result.setValue(Double.parseDouble(this.literal.associatedCharacters()));
        }

        return result;
    }

    public void print(int depth) {
        System.out.printf("%" + (depth + 1) + "s%s\n", "", this.literal.associatedCharacters());
    }
}