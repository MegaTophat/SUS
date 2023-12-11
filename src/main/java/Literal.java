public class Literal implements ParseTree {
    private final Lexeme literal;

    Literal(final Lexeme literal) {
        this.literal = literal;
    }

    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        final EvaluationResult result = new EvaluationResult();

        result.setValue(
                Double.parseDouble(
                        this.literal.associatedCharacters()
                )
        );

        return result;
    }
}