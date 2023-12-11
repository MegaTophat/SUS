import java.util.ArrayList;
import java.util.List;

public class ArrayDimension implements ParseTree {
    public EvaluationResult evaluate(final ReferenceEnvironment referenceEnvironment) {
        //create the list
        final List<EvaluationResult> list = new SusList();
        final EvaluationResult value = new EvaluationResult();

        value.setValue(list);

        return value;
    }

    private static final class SusList extends ArrayList<EvaluationResult> {
        private SusList() {
            super();
        }

        @Override
        public EvaluationResult set(final int index, final EvaluationResult element) {
            while (super.size() <= index) {
                // filler to stop index out of bounds exceptions
                final EvaluationResult filler = new EvaluationResult();
                super.add(filler);
            }

            return super.set(index, element);
        }
    }
}