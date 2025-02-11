public class Conditional extends BinaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        if (this.getLeft().evaluate(referenceEnvironment).asBoolean()) {
            this.getRight().evaluate(referenceEnvironment);
        }

        return new EvaluationResult();
    }
}