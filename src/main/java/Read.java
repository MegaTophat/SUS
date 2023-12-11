import java.util.Scanner;

public class Read extends UnaryOp {
    private static final Scanner SCANNER = new Scanner(System.in);

    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        final Variable variable = (Variable) getChild();
        final String line = SCANNER.nextLine();
        final EvaluationResult value = new EvaluationResult();

        try {
            final double doubleInput = Double.parseDouble(line);

            value.setValue(doubleInput);
        } catch (final NumberFormatException ex) {
            value.setValue(line);
        }

        variable.set(referenceEnvironment, value);

        return null;
    }
}