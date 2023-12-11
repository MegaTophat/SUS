public class Parser {
    private final Lexer lexer;

    public Parser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public ParseTree parse() {
        this.lexer.nextLexeme();  //linitialize the lexer

        return this.parseBlock();
    }

    /**
     * Attempt to match token.
     * If it matches, return the lexeme. If it does not match, return null.
     */
    private Lexeme match(final TokenType token) {
        final Lexeme lexeme = this.lexer.currentLexeme();

        if (lexeme.tokenType() == token) {
            // match, advance lexer, return the match
            this.lexer.nextLexeme();

            return lexeme;
        }

        // no match
        return null;
    }

    /**
     * Attempt to match a token. Returns lexeme on match, halts the program on failure.
     */
    private Lexeme mustBe(TokenType token) {
        final Lexeme lexeme = this.match(token);

        if (lexeme == null) {
            System.out.println("Parse Error: " + lexer.currentLexeme().toString());
            System.exit(-1);
        }

        return lexeme;
    }

    /**
     * Return true if one of the tokenTypeTypes in the list are
     * currently in the lexer
     */
    private boolean has(final TokenType... tokenTypeTypes) {
        final Lexeme lexeme = this.lexer.currentLexeme();

        for (final TokenType tokenType : tokenTypeTypes) {
            if (lexeme.tokenType() == tokenType) {
                return true;
            }
        }

        return false;
    }

    /**
     * < Block > ::= < Block > < Statement >
     * | < Statement >
     */
    private ParseTree parseBlock() {
        final Block result = new Block();

        while (this.lexer.currentLexeme().tokenType() != TokenType.EOF && this.lexer.currentLexeme().tokenType() != TokenType.SWOTUS) {
            final ParseTree statement = this.parseStatement();

            if (statement != null) {
                result.addChild(statement);
            }
        }

        return result;
    }

    /**
     * < Statement > ::= ID < Statement' > NEWLINE
     * | < IO-Operation > NEWLINE
     * | < Array-Dim > NEWLINE
     * | < Conditional > NEWLINE
     * | < SusMoney > NEWLINE
     * | < Expression > NEWLINE
     * | < Comment > NEWLINE
     * | NEWLINE
     */
    private ParseTree parseStatement() {
        ParseTree result = null;

        if (this.has(TokenType.VARIABLE_NAME)) {
            // Handle an ID statement / expression
            result = this.parseRef();
            result = this.parseStatement2(result);
        } else if (this.has(TokenType.READ, TokenType.PRINT)) {
            result = this.parseIOOperation();
        } else if (this.has(TokenType.ARRAY)) {
            result = this.parseArrayDimension();
        } else if (this.has(TokenType.SUS)) {
            result = this.parseConditional();
        } else if (this.has(TokenType.SUSMONEY)) {
            result = this.parseLoop();
        } else if (this.has(TokenType.COMMENT)) {
            result = this.parseComment();
        } else if (!this.has(TokenType.NEWLINE)) {
            result = this.parseExpression();
        }

        if (this.match(TokenType.EOF) == null) {
            this.mustBe(TokenType.NEWLINE);
        }
        return result;
    }

    private ParseTree parseComment() {
        this.mustBe(TokenType.COMMENT);

        return new Comment();
    }

    /**
     * < Statement' > ::= EQUAL < Expression >
     * | < Factor' > < Term' > < Expression' >
     */
    private ParseTree parseStatement2(final ParseTree left) {
        // match an assignment
        if (this.match(TokenType.EQUAL) != null) {
            final Assignment result = new Assignment();

            result.setLeft(left);
            result.setRight(this.parseExpression());

            return result;
        }

        // an expression beginning with an ID
        ParseTree result = this.parseFactor2(left);
        result = this.parseTerm2(result);
        result = this.parseExpression2(result);

        return result;
    }

    /**
     * < Conditional > ::= IF < Condition > NEWLINE < Block > END IF
     */
    private ParseTree parseConditional() {
        this.mustBe(TokenType.SUS);
        final ParseTree condition = this.parseCondition();
        this.mustBe(TokenType.SWAUS);
        this.mustBe(TokenType.NEWLINE);
        final ParseTree program = this.parseBlock();
        this.mustBe(TokenType.SWOTUS);

        final Conditional result = new Conditional();

        result.setLeft(condition);
        result.setRight(program);

        return result;
    }

    /**
     * < SusMoney > ::= sus < Condition > swaus < Block > swotus
     */
    private ParseTree parseLoop() {
        this.mustBe(TokenType.SUSMONEY);
        final ParseTree condition = this.parseCondition();
        this.mustBe(TokenType.SWAUS);
        this.mustBe(TokenType.NEWLINE);
        final ParseTree program = this.parseBlock();
        this.mustBe(TokenType.SWOTUS);

        final SusMoney result = new SusMoney();

        result.setLeft(condition);
        result.setRight(program);

        return result;
    }

    /**
     * < Condition > ::= < Expression > < Condition' >
     */
    private ParseTree parseCondition() {
        final ParseTree left = this.parseExpression();

        return this.parseCondition2(left);
    }

    /**
     * < Condition' > ::= EQUAL < Expression >
     * | GT < Expression >
     * | LT < Expression >
     * | LTE < Expression >
     * | GTE < Expression >
     * | EQUAL < Expression >
     * | NE < Expression >
     */
    private ParseTree parseCondition2(final ParseTree left) {
        final BinaryOp result;

        if (this.match(TokenType.GREATER_THAN) != null) {
            result = new Greater();
        } else if (this.match(TokenType.GREATER_THAN_OR_EQUAL) != null) {
            result = new GreaterOrEqual();
        } else if (this.match(TokenType.LESS_THAN) != null) {
            result = new Less();
        } else if (this.match(TokenType.LESS_THAN_OR_EQUAL) != null) {
            result = new LessOrEqual();
        } else if (this.match(TokenType.EQUAL) != null) {
            result = new Equal();
        } else if (this.mustBe(TokenType.NOT_EQUAL) != null) {
            result = new NotEqual();
        } else {
            return null;
        }

        result.setLeft(left);
        result.setRight(this.parseExpression());

        return result;
    }

    private ParseTree parseArrayDimension() {
        return new ArrayDimension();
    }

    /**
     * < IO-Operation > ::= PRINT < Expression >
     * | READ < Ref >
     */
    private ParseTree parseIOOperation() {
        if (this.match(TokenType.PRINT) != null) {
            final Print result = new Print();

            result.setChild(this.parseExpression());

            return result;
        }

        this.mustBe(TokenType.READ);

        final Read result = new Read();

        result.setChild(this.parseRef());

        return result;
    }

    /**
     * < Term > ::= < Factor > < Term' >
     */
    private ParseTree parseTerm() {
        final ParseTree left = this.parseFactor();

        return this.parseTerm2(left);
    }

    /**
     * < Term' > ::= TIMES < Factor > < Term' >
     * | DIVIDE < Factor > < Term' >
     * | MOD < Factor > < Term' >
     * | ""
     */
    private ParseTree parseTerm2(final ParseTree left) {
        if (this.match(TokenType.TIMES) != null) {
            final Multiply result = new Multiply();

            result.setLeft(left);
            result.setRight(this.parseFactor());
            return this.parseTerm2(result);
        } else if (this.match(TokenType.DIVIDE) != null) {
            final Divide result = new Divide();

            result.setLeft(left);
            result.setRight(this.parseTerm());

            return this.parseTerm2(result);

        } else if (this.match(TokenType.REMAINDER) != null) {
            final Remainder result = new Remainder();

            result.setLeft(left);
            result.setRight(this.parseFactor());

            return this.parseTerm2(result);
        }

        return left;
    }

    /**
     * < Number > ::= REALLIT
     * | < Ref >
     */
    private ParseTree parseNumber() {
        if (this.has(TokenType.VARIABLE_NAME)) {
            return this.parseRef();
        }

        final Lexeme token = this.mustBe(TokenType.NUMBER);

        return new Literal(token);
    }

    /*
     * < Ref > ::= ID < Ref' >
     */
    private ParseTree parseRef() {
        final Lexeme token = this.mustBe(TokenType.VARIABLE_NAME);

        return this.parseRef2(new Variable(token));
    }

    /*
     * < Ref' > ::= ` < Expression > ` < Ref' >
     *            | ""
     */
    private ParseTree parseRef2(final ParseTree left) {
        if (this.match(TokenType.ARRAY_START) != null) {
            // array access
            final ArrayAccess result = new ArrayAccess();

            result.setLeft(left);
            result.setRight(this.parseExpression());

            this.mustBe(TokenType.ARRAY_END);

            return this.parseRef2(result);
        } else if (this.match(TokenType.ARRAY) != null) {
            return this.parseArrayDimension();
        }

        // null string
        return left;
    }

    /**
     * < Exponent > ::= < Number >
     * | MINUS < Exponent >
     * | LPAREN < Expression > RPAREN
     */
    private ParseTree parseExponent() {
        if (this.match(TokenType.LPAREN) != null) {
            final ParseTree result = this.parseExpression();

            this.mustBe(TokenType.RPAREN);

            return result;
        } else {
            return this.parseNumber();
        }
    }

    /*
     * < Expression > ::= < Term > < Expression' >
     *                  | < String >
     */
    private ParseTree parseExpression() {
        final Lexeme token;

        if ((token = this.match(TokenType.STRING)) != null) {
            return new SusString(token);
        } else if (this.match(TokenType.ARRAY) != null) {
            return this.parseArrayDimension();
        }

        final ParseTree left = this.parseTerm();

        return this.parseExpression2(left);
    }

    /**
     * < Expression' > ::= PLUS < Term > < Expression' >
     * | MINUS < Term > < Expression' >
     * | ""
     */
    public ParseTree parseExpression2(final ParseTree left) {
        if (this.match(TokenType.PLUS) != null) {
            final Add result = new Add();

            result.setLeft(left);
            result.setRight(this.parseTerm());

            return this.parseExpression2(result);
        } else if (this.match(TokenType.MINUS) != null) {
            final Subtract result = new Subtract();

            result.setLeft(left);
            result.setRight(this.parseTerm());

            return this.parseExpression2(result);
        }

        return left;
    }

    /*
     * < Factor > ::= < Exponent > < Factor' >
     */
    public ParseTree parseFactor() {
        final ParseTree left = this.parseExponent();
        return this.parseFactor2(left);
    }

    /**
     * < Factor' > ::= POW < Exponent > < Factor' >
     * | ""
     */
    public ParseTree parseFactor2(final ParseTree left) {
        if (this.match(TokenType.POW) != null) {
            final Power result = new Power();

            result.setLeft(left);
            result.setRight(this.parseExponent());

            return this.parseFactor2(result);
        }

        return left;
    }
}
