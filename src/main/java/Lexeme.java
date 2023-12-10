record Lexeme(TokenType tokenType, String associatedCharacters, int lineNum, int columnNum) {
    public String toString() {
        return this.tokenType +
                " at line: " + this.lineNum +
                " column: " + this.columnNum +
                " (" + this.associatedCharacters +
                ')';
    }
}