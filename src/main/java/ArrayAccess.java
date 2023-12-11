import java.util.List;

public class ArrayAccess extends BinaryOp {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        final Variable arrayVariable = (Variable) this.getLeft();
        final String arrayVariableName = arrayVariable.name();
        final int elementToGet = (int) this.getRight().evaluate(referenceEnvironment).asNumber();
        final List<EvaluationResult> actualList = referenceEnvironment.getVariable(arrayVariableName).asList();

        return actualList.get(elementToGet);
    }
}
