public class LessOrEqual extends BinaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        // get the operands
        final EvaluationResult l = this.getLeft().evaluate(referenceEnvironment);
        final EvaluationResult r = this.getRight().evaluate(referenceEnvironment);
        final EvaluationResult result = new EvaluationResult();

        result.setValue(l.asNumber() <= r.asNumber());

        return result;
    }
}