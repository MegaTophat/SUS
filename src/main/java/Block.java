public class Block extends NaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        EvaluationResult result = null;

        // execute each child
        for (int i = 0; i < this.getSize(); i++) {
            result = this.getChild(i).evaluate(referenceEnvironment);
        }

        return result;
    }
}