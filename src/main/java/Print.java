import java.util.List;

public class Print extends UnaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        this.display(
                this.getChild().evaluate(referenceEnvironment)
        );

        return null;
    }

    private void display(final EvaluationResult result) {
        if (result.getEvaluationType() == EvaluationType.ARRAY) {
            this.display_array(result.asList());
        } else if (result.getEvaluationType() == EvaluationType.STRING) {
            final String string = result.asString();
            System.out.println(string.substring(1, string.length() - 1));
        } else {
            System.out.println(result);
        }
    }

    private void display_array(final List<EvaluationResult> array) {
        for (final EvaluationResult r : array) {
            this.display(r);
        }
    }
}
