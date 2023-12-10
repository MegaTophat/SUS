public class EvalResult 
{
  private EvalType type;
  private double n;
  private String s;
  private boolean b;
  private RecordDeclaration recordDecl;
  private EvalResult [] array;

  public EvalResult() {
    type = EvalType.VOID;
  }
  
  // set the value of the object and infer its type
  public void setValue(double value) {
    type = EvalType.NUMBER;
    n=value;
  }

  public void setValue(String value) {
    type = EvalType.STRING;
    s = value;
  }


  public void setValue(boolean value) {
    type = EvalType.BOOLEAN;
    b = value;
  }


  public void setValue(RecordDeclaration recordDecl) {
    type = EvalType.RECORD_DECL;
    this.recordDecl = recordDecl;
  }

  public void setValue(EvalResult [] value) {
    type = EvalType.ARRAY;
    array = value;
  }


  public double asInteger() {
    return n;
  }


  public double asReal() {
    return n;
  }


  public boolean asBoolean() {
    return b;
  }

  public RecordDeclaration asRecordDecl() {
    return recordDecl;
  }

  public EvalResult [] asArray() {
    if(type != EvalType.ARRAY) return null;
    return array;
  }


  public EvalType getType() {
    return type;
  }




  public String toString() {
    return ""+n;
  }
}
