import java.util.HashMap;

public class ReferenceEnvironment {
    private final HashMap<String, EvaluationResult> variableNameMap;

    public ReferenceEnvironment() {
        this.variableNameMap = new HashMap<>();
    }

    // store a variable
    public void setVariable(final String variableName, final EvaluationResult newValue) {
        this.variableNameMap.put(variableName, newValue);
    }

    // retrieve a variable
    public EvaluationResult getVariable(final String variableName) {
        return this.variableNameMap.get(variableName);
    }
}