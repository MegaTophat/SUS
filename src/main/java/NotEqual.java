public class NotEqual extends BinaryOp {
    public EvaluationResult evaluate(ReferenceEnvironment referenceEnvironment) {
        final EvaluationResult l = this.getLeft().evaluate(referenceEnvironment);
        final EvaluationResult r = this.getRight().evaluate(referenceEnvironment);
        final EvaluationResult result = new EvaluationResult();

        result.setValue(l.asNumber() != r.asNumber());

        return result;
    }
}