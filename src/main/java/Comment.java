public final class Comment implements ParseTree {
    private final Lexeme comment;

    public Comment(final Lexeme comment) {
        this.comment = comment;
    }

    @Override
    public EvalResult eval(RefEnv env) {
        return new EvalResult();
    }

    @Override
    public void print(int depth) {
        System.out.printf("%" + (depth + 1) + "s%s\n", "", this.comment.associatedCharacters());
    }
}
