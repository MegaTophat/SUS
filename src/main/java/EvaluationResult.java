import java.util.List;

public class EvaluationResult {
    private EvaluationType evaluationType;
    private double number;
    private String string;
    private boolean bool;
    private List<EvaluationResult> array;

    public EvaluationResult() {
        this.evaluationType = EvaluationType.VOID;
    }

    // set the value of the object and infer its type
    public void setValue(final double value) {
        this.evaluationType = EvaluationType.NUMBER;
        this.number = value;
    }

    public void setValue(final String value) {
        this.evaluationType = EvaluationType.STRING;
        this.string = value;
    }

    public void setValue(final boolean value) {
        this.evaluationType = EvaluationType.BOOLEAN;
        this.bool = value;
    }

    public void setValue(final List<EvaluationResult> value) {
        this.evaluationType = EvaluationType.ARRAY;
        this.array = value;
    }

    public double asNumber() {
        return this.number;
    }

    public boolean asBoolean() {
        return this.bool;
    }

    public String asString() {
        return this.string;
    }

    public List<EvaluationResult> asList() {
        if (this.evaluationType != EvaluationType.ARRAY) {
            return null;
        }

        return this.array;
    }

    public EvaluationType getEvaluationType() {
        return this.evaluationType;
    }

    public String toString() {
        return switch (this.evaluationType) {
            case NUMBER -> String.valueOf(this.number);
            case VOID -> "void";
            case STRING -> this.string;
            case ARRAY -> this.array.toString();
            case BOOLEAN -> String.valueOf(this.bool);
        };
    }
}
