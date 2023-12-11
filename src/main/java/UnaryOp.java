public abstract class UnaryOp implements ParseTree {
    private ParseTree child;

    public ParseTree getChild() {
        return this.child;
    }

    public void setChild(final ParseTree child) {
        this.child = child;
    }
}