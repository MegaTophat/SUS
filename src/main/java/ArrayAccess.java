public class ArrayAccess extends BinaryOp
{
  public EvalResult eval(RefEnv env)
  {
    return null;
  }

  public void print(int depth) 
  {
    getRight().print(depth+1);
    System.out.printf("%"+(depth+1)+"s[] (array index)\n", "");
    getLeft().print(depth+1);
  }
}
