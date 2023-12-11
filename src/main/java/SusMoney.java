public class SusMoney extends BinaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        while (this.getLeft().evaluate(referenceEnvironment).asBoolean()) {
            this.getRight().evaluate(referenceEnvironment);
        }

        return new EvaluationResult();
    }
}