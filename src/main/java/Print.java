public class Print extends UnaryOp {
  public EvalResult eval(RefEnv env) {
    display(getChild().eval(env));
    return null;
  }

  private void display(EvalResult result) {
    if(result.getType() == EvalType.ARRAY) {
      display_array(result.asArray());
    } else if (result.getType() == EvalType.STRING) {
      final String string = result.asString();
      System.out.println(string.substring(1, string.length() - 1));
    } else {
      System.out.println(result);
    }
  }

  private void display_array(EvalResult[] array) {
    for(EvalResult r : array) {
      display(r);
    }
  }

  public void print(int depth) {
    getChild().print(depth + 1);
    System.out.printf("%" + (depth + 1) + "sprint\n", "");
  }
}
