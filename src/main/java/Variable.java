public class Variable implements ParseTree {
    private final Lexeme token;

    Variable(final Lexeme token) {
        this.token = token;
    }

    public EvaluationResult evaluate(ReferenceEnvironment referenceEnvironment) {
        return referenceEnvironment.getVariable(this.token.associatedCharacters());
    }

    public String name() {
        return this.token.associatedCharacters();
    }


    public void set(final ReferenceEnvironment referenceEnvironment, EvaluationResult newValue) {
        referenceEnvironment.setVariable(this.token.associatedCharacters(), newValue);
    }
}