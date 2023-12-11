import java.util.List;

public class Assignment extends BinaryOp
{
  public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
    final ParseTree left = getLeft();

    if (left instanceof ArrayAccess arrayAccess) {
      final Variable listModifyingVariable = (Variable) arrayAccess.getLeft();
      final String listModifyingName = listModifyingVariable.name();
      final List<EvaluationResult> actualList = referenceEnvironment.getVariable(listModifyingName).asList();
      final int elementIndex = (int) arrayAccess.getRight().evaluate(referenceEnvironment).asNumber();
      final EvaluationResult thingToSet = this.getRight().evaluate(referenceEnvironment);

      actualList.set(elementIndex, thingToSet);

      return null;
    }

    final Variable variable = (Variable) left;
    
    // set the variable name to it's value in the map
    variable.set(referenceEnvironment, this.getRight().evaluate(referenceEnvironment));

    return null;
  }
}