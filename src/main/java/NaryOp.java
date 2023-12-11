import java.util.ArrayList;

public abstract class NaryOp implements ParseTree {
    protected final ArrayList<ParseTree> children;

    public NaryOp() {
        this.children = new ArrayList<>();
    }

    public void addChild(final ParseTree child) {
        this.children.add(child);
    }

    public ParseTree getChild(final int index) {
        // protect against index errors
        if (index < 0 || index >= children.size()) {
            return null;
        }

        return this.children.get(index);
    }

    public int getSize() {
        return this.children.size();
    }
}