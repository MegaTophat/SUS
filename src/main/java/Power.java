public class Power extends BinaryOp
{
  public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
    // get the operands
    final double l = this.getLeft().evaluate(referenceEnvironment).asNumber();
    final double r = this.getRight().evaluate(referenceEnvironment).asNumber();
    final double x = Math.pow(l, r);
    final EvaluationResult result = new EvaluationResult();

    result.setValue(x);
    
    return result;
  }
}