public class Assignment extends BinaryOp
{
  public EvalResult eval(RefEnv env) {
    Variable v = (Variable) getLeft();
    
    // set the variable to the result of the right hand side
    v.set(env, getRight().eval(env));
    return null;
  }


  public void print(int depth)  {
    getRight().print(depth+1);
    System.out.printf("%"+(depth+1)+"s= (assign)\n", "");
    getLeft().print(depth+1);
  }
}