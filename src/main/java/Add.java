public class Add extends BinaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        // get the operands
        final EvaluationResult l = getLeft().evaluate(referenceEnvironment);
        final EvaluationResult r = getRight().evaluate(referenceEnvironment);
        final EvaluationResult result = new EvaluationResult();
        final double x = l.asNumber() + r.asNumber();

        result.setValue(x);

        return result;
    }
}