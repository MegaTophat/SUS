import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Test the parser
     */
    public static void main(String[] args) throws IOException {
        final InputStream inputStream;

        if (args.length > 0) {
            final String workingDirectory = System.getProperty("user.dir");
            final File file = new File(workingDirectory, args[0]);

            inputStream = new FileInputStream(file);
        } else {
            inputStream = System.in;
        }
        Lexer lexer = new Lexer(inputStream);
        Parser parser = new Parser(lexer);

        parser.parse().print(0);
    }

    public ParseTree parse() {
        this.lexer.nextLexeme();  //linitialize the lexer
        return parseBlock();
    }

    /**
     * Attempt to match tok.
     * If it matches, return the lexeme. If it does not match, return null.
     */
    private Lexeme match(TokenType tok) {
        Lexeme lexeme = this.lexer.currentLexeme();
        if (lexeme.tokenType() == tok) {
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
    private Lexeme mustBe(TokenType tok) {
        final Lexeme lexeme = match(tok);

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
    private boolean has(TokenType... tokenTypeTypes) {
        final Lexeme lexeme = this.lexer.currentLexeme();

        for (final TokenType tokenType : tokenTypeTypes) {
            if (lexeme.tokenType() == tokenType) {
                return true;
            }
        }

        return false;
    }

    /**
     * < Branch > ::= < Branch > < Statement >
     * | < Statement >
     */
    private ParseTree parseBlock() {
        final Branch result = new Branch();

        while (this.lexer.currentLexeme().tokenType() != TokenType.EOF && this.lexer.currentLexeme().tokenType() != TokenType.SWOTUS) {
            ParseTree statement = parseStatement();
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
     * | < Loop > NEWLINE
     * | < Expression > NEWLINE
     * | < Comment > NEWLINE
     * | NEWLINE
     */
    private ParseTree parseStatement() {
        ParseTree result = null;
        Lexeme tok;

        if (has(TokenType.VARIABLE_NAME)) {
            // Handle an ID statement / expression
            result = parseRef();
            result = parseStatement2(result);
        } else if (has(TokenType.READ, TokenType.PRINT)) {
            result = parseIOOperation();
        } else if (has(TokenType.SUS)) {
            result = parseConditional();
        } else if (has(TokenType.SUSMONEY)) {
            result = parseLoop();
        } else if (has(TokenType.COMMENT)) {
                result = parseComment();
        } else if (!has(TokenType.NEWLINE)) {
            result = parseExpression();
        }

        if (match(TokenType.EOF) == null) {
            mustBe(TokenType.NEWLINE);
        }
        return result;
    }

    private ParseTree parseComment() {
        final Lexeme lexeme = mustBe(TokenType.COMMENT);

        return new Comment(lexeme);
    }

    /**
     * < Statement' > ::= EQUAL < Expression >
     * | < Factor' > < Term' > < Expression' >
     */
    private ParseTree parseStatement2(ParseTree left) {
        // match an assignment
        if (match(TokenType.EQUAL) != null) {
            Assignment result = new Assignment();
            result.setLeft(left);
            result.setRight(parseExpression());
            return result;
        }

        // an expression beginning with an ID
        ParseTree result = parseFactor2(left);
        result = parseTerm2(result);
        result = parseExpression2(result);
        return result;
    }

    /**
     * < Conditional > ::= IF < Condition > NEWLINE < Branch > END IF
     */
    private ParseTree parseConditional() {
        mustBe(TokenType.SUS);
        ParseTree condition = parseCondition();
        mustBe(TokenType.NEWLINE);
        ParseTree program = parseBlock();
        mustBe(TokenType.SWOTUS);

        Conditional result = new Conditional();
        result.setLeft(condition);
        result.setRight(program);
        return result;
    }

    /**
     * < Loop > ::= sus < Condition > swaus < Branch > swotus
     */
    private ParseTree parseLoop() {
        mustBe(TokenType.SUSMONEY);
        ParseTree condition = parseCondition();
        mustBe(TokenType.NEWLINE);
        ParseTree program = parseBlock();
        mustBe(TokenType.SWOTUS);

        Loop result = new Loop();
        result.setLeft(condition);
        result.setRight(program);

        return result;
    }

    /**
     * < Condition > ::= < Expression > < Condition' >
     */
    private ParseTree parseCondition() {
        ParseTree left = parseExpression();
        return parseCondition2(left);
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
    private ParseTree parseCondition2(ParseTree left) {
        final BinaryOp result;

        if (match(TokenType.GREATER_THAN) != null) {
            result = new Greater();
        } else if (match(TokenType.LESS_THAN) != null) {
            result = new Less();
        } else if (match(TokenType.LESS_THAN_OR_EQUAL) != null) {
            result = new LessOrEqual();
        } else if (match(TokenType.EQUAL) != null) {
            result = new Equal();
        } else if (mustBe(TokenType.NOT_EQUAL) != null) {
            result = new NotEqual();
        } else {
            return null;
        }

        result.setLeft(left);
        result.setRight(parseExpression());

        return result;
    }

    /**
     * < IO-Operation > ::= PRINT < Expression >
     * | READ < Ref >
     */
    private ParseTree parseIOOperation() {
        if (match(TokenType.PRINT) != null) {
            Print result = new Print();
            result.setChild(parseExpression());
            return result;
        }

        mustBe(TokenType.READ);
        Read result = new Read();
        result.setChild(parseRef());
        return result;
    }

    /**
     * < Term > ::= < Factor > < Term' >
     */
    private ParseTree parseTerm() {
        ParseTree left = parseFactor();
        return parseTerm2(left);
    }

    /**
     * < Term' > ::= TIMES < Factor > < Term' >
     * | DIVIDE < Factor > < Term' >
     * | MOD < Factor > < Term' >
     * | ""
     */
    private ParseTree parseTerm2(ParseTree left) {
        if (match(TokenType.TIMES) != null) {
            Multiply result = new Multiply();
            result.setLeft(left);
            result.setRight(parseFactor());
            return parseTerm2(result);
        } else if (match(TokenType.DIVIDE) != null) {
            Divide result = new Divide();
            result.setLeft(left);
            result.setRight(parseTerm());
            return parseTerm2(result);

        } else if (match(TokenType.REMAINDER) != null) {
            Remainder result = new Remainder();
            result.setLeft(left);
            result.setRight(parseFactor());
            return parseTerm2(result);
        }

        return left;
    }

    /**
     * < Number > ::= INTLIT
     * | REALLIT
     * | < Ref >
     */
    private ParseTree parseNumber() {
        Lexeme tok = match(TokenType.NUMBER);
        if (tok != null) {
            return new Literal(tok);
        }

        if (has(TokenType.VARIABLE_NAME)) {
            return parseRef();
        }

        tok = mustBe(TokenType.NUMBER);
        return new Literal(tok);
    }

    /*
     * < Ref > ::= ID < Ref' >
     */
    ParseTree parseRef() {
        Lexeme tok = mustBe(TokenType.VARIABLE_NAME);

        return parseRef2(new Variable(tok));
    }

    /*
     * < Ref' > ::= LBRACKET < Expression > RBRACKET < Ref' >
     *            | DOT ID < Ref' >
     *            | ""
     */
    ParseTree parseRef2(ParseTree left) {
        if (match(TokenType.ARRAY_BOUNDARY) != null) {
            // array access
            ArrayAccess result = new ArrayAccess();
            result.setLeft(left);
            result.setRight(parseExpression());
            mustBe(TokenType.ARRAY_BOUNDARY);
            return parseRef2(result);
        } else if (match(TokenType.DOT) != null) {
            // record access
            RecordAccess result = new RecordAccess();
            result.setLeft(left);
            result.setRight(new Variable(mustBe(TokenType.VARIABLE_NAME)));
            return parseRef2(result);
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
        if (match(TokenType.MINUS) != null) {
            Negate result = new Negate();
            result.setChild(parseExponent());
            return result;
        } else if (match(TokenType.LPAREN) != null) {
            ParseTree result = parseExpression();
            mustBe(TokenType.RPAREN);
            return result;
        } else {
            return parseNumber();
        }
    }

    /*
     * < Expression > ::= < Term > < Expression' >
     *                  | < String >
     */
    private ParseTree parseExpression() {
        Lexeme token = match(TokenType.STRING);

        if (token != null) {
            return new SusString(token);
        }

        ParseTree left = parseTerm();
        return parseExpression2(left);
    }

    /**
     * < Expression' > ::= PLUS < Term > < Expression' >
     * | MINUS < Term > < Expression' >
     * | ""
     */
    public ParseTree parseExpression2(ParseTree left) {
        if (match(TokenType.PLUS) != null) {
            Add result = new Add();
            result.setLeft(left);
            result.setRight(parseTerm());
            return parseExpression2(result);
        } else if (match(TokenType.MINUS) != null) {
            Subtract result = new Subtract();
            result.setLeft(left);
            result.setRight(parseTerm());
            return parseExpression2(result);
        }

        // ""
        return left;
    }

    /*
     * < Factor > ::= < Exponent > < Factor' >
     */
    public ParseTree parseFactor() {
        ParseTree left = parseExponent();
        return parseFactor2(left);
    }

    /**
     * < Factor' > ::= POW < Exponent > < Factor' >
     * | ""
     */
    public ParseTree parseFactor2(ParseTree left) {
        if (match(TokenType.POW) != null) {
            Power result = new Power();
            result.setLeft(left);
            result.setRight(parseExponent());
            return parseFactor2(result);
        }

        return left;
    }
}
