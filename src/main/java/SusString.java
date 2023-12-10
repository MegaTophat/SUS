public final class SusString implements ParseTree {
    private final Lexeme stringLexeme;

    SusString(final Lexeme stringLexeme) {
        this.stringLexeme = stringLexeme;
    }

    @Override
    public EvalResult eval(final RefEnv env) {
        final EvalResult evalResult = new EvalResult();
        evalResult.setValue(this.stringLexeme.associatedCharacters());

        return evalResult;
    }

    @Override
    public void print(final int depth) {
        System.out.printf("%" + (depth + 1) + "s%s\n", "", this.stringLexeme.associatedCharacters());
    }
}
