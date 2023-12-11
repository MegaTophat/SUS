public abstract class BinaryOp implements ParseTree {
    private ParseTree left;
    private ParseTree right;

    // get the left child
    public ParseTree getLeft() {
        return this.left;
    }

    // set the left child
    public void setLeft(final ParseTree left) {
        this.left = left;
    }

    // get the right child
    public ParseTree getRight() {
        return this.right;
    }

    // set the right child
    public void setRight(final ParseTree right) {
        this.right = right;
    }
}