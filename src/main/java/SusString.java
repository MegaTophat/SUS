public final class SusString implements ParseTree {
    private final Lexeme stringLexeme;

    SusString(final Lexeme stringLexeme) {
        this.stringLexeme = stringLexeme;
    }

    @Override
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        final EvaluationResult evaluationResult = new EvaluationResult();

        evaluationResult.setValue(this.stringLexeme.associatedCharacters());

        return evaluationResult;
    }
}
