public class Variable implements ParseTree {
  private Lexeme tok;

  Variable(Lexeme tok) {
    this.tok = tok;
  }

  public EvalResult eval(RefEnv env) {
    return env.getVariable(this.tok.associatedCharacters());
  }

  public String name() {
    return tok.associatedCharacters();
  }


  public void set(RefEnv env, EvalResult val) {
    env.setVariable(this.tok.associatedCharacters(), val);
  }

  public void print(int depth) {
    System.out.printf("%" + (depth + 1) + "s%s\n", "", tok.associatedCharacters());
  }
}