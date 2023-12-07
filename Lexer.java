import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Lexer for the calc language
 */
public class Lexer {
    /// Source of the character stream
    private final InputStream file;

    /// Current character being matched
    private char cur;

    /// Current Lexeme
    private Lexeme curLex;

    /// Line and column number of input
    private int line;
    private int col;
    private int startLine;
    private int startCol;
    private boolean eof;

    /// The lexeme that we are accumulating
    private StringBuilder curString;

    /// Construct a lexer for the given input stream
    public Lexer(InputStream file) {
        this.file = file;
        this.line = 1;
        this.col = 0;
        this.eof = false;
        read();
    }

    public static void main(String[] args) throws FileNotFoundException {
        final InputStream inputStream;

        if (args.length > 0) {
            final String workingDirectory = System.getProperty("user.dir");
            File file = new File(workingDirectory, args[0]);
            inputStream = new FileInputStream(file);
        } else {
            inputStream = System.in;
        }

        Lexer lex = new Lexer(inputStream);
        Lexeme tok;

        do {
            tok = lex.next();
            System.out.println(tok);
        } while (tok.tok != TokenType.EOF);
    }

    private void setToken(TokenType tok) {
        curLex = new Lexeme(tok, curString.toString(), startLine, startCol);
    }

    // skip characters we wish to ignore
    private void skip() {
        // skip all the blank characters
        while (cur != '\n' && (Character.isSpaceChar(cur))) {
            read();
        }
    }

    // skip to the end of the line
    private void skipToEOL() {
        while (!eof && cur != '\n') {
            read();
        }
    }

    Lexeme next() {
        // start the matching
        skip();
        curString = new StringBuilder();
        startLine = line;
        startCol = col;

        // handle eof
        if (eof) {
            setToken(TokenType.EOF);
            return curLex;
        }

        if (match_single())
            return curLex;
        else if (match_number())
            return curLex;
        else if (matchWord())
            return curLex;
        else if (matchFixed())
            return curLex;
        else if (matchString()) {
            return curLex;
        } else
            consume();

        // invalid
        setToken(TokenType.UNKNOWN);
        return curLex;
    }

    Lexeme cur() {
        return curLex;
    }

    /// Match our single character tokens
    /// Return true on success, false on failure.
    public boolean match_single() {
        final TokenType tok = switch (cur) {
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '*' -> TokenType.TIMES;
            case '/' -> TokenType.DIVIDE;
            case '^' -> TokenType.POW;
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case '\n' -> TokenType.NEWLINE;
            case '=' -> TokenType.EQUAL;
            case '.' -> TokenType.DOT;
            default -> TokenType.UNKNOWN;
        };

        // did not match
        if (tok == TokenType.UNKNOWN) {
            return false;
        }

        // match
        consume();
        setToken(tok);
        return true;
    }

    /// Match an integer
    private void consumeInteger() {
        while (Character.isDigit(cur)) {
            consume();
        }
    }

    private void consumeString() {
        while (this.cur != '"' && this.cur != '\n' && !this.eof) {
            consume();
        }
    }

    private boolean matchString() {
        if (this.cur != '"') {
            return false;
        }
        consume();
        consumeString();

        if (this.cur != '"') {
            setToken(TokenType.UNKNOWN);
            return false;
        }

        // consume the ending " symbol
        consume();
        setToken(TokenType.STRING);
        return true;
    }

    /// Match a Number
    private boolean match_number() {
        if (!Character.isDigit(cur))
            return false;

        // this is an integer
        consumeInteger();

        // check for a dot
        if (cur != '.') {
            setToken(TokenType.NUMBER);
            return true;
        }

        // consume the singular dot
        consume();
        if (Character.isDigit(cur)) {
            consumeInteger();
            setToken(TokenType.NUMBER);
        } else {
            setToken(TokenType.UNKNOWN);
        }

        return true;
    }

    private boolean matchWord() {
        if (!Character.isAlphabetic(cur) && cur != '_')
            return false;

        // consume letters numbers and _
        while (Character.isDigit(cur) || Character.isAlphabetic(cur) || cur == '_') {
            consume();
        }

        // match keywords, then variable names as lowest priority
        final String str = curString.toString();
        switch (str) {
            case "MOD" -> setToken(TokenType.MOD);
            case "PRINT" -> setToken(TokenType.PRINT);
            case "READ" -> setToken(TokenType.READ);
            case "sus" -> setToken(TokenType.SUS);
            case "swotus" -> setToken(TokenType.SWOTUS);
            case "susmoney" -> setToken(TokenType.SUSMONEY);
            case "ARRAY" -> setToken(TokenType.ARRAY);
            default ->
                //if it's not a keyword, it's a valid variable name
                    setToken(TokenType.VARIABLE_NAME);
        }

        return true;
    }

    /// Match a multi-character but fixed width
    /// token
    private boolean matchFixed() {
        if (cur == '<') {
            consume();
            setToken(TokenType.LESS_THAN);

            // check for the second part
            if (cur == '=') {
                consume();
                setToken(TokenType.LESS_THAN_OR_EQUAL);
            }

            return true;
        } else if (cur == '>') {
            consume();
            setToken(TokenType.GREATER_THAN);
            if (cur == '=') {
                consume();
                setToken(TokenType.GREATER_THAN_OR_EQUAL);
            }

            return true;
        } else if (cur == '!') {
            consume();
            if (cur == '=') {
                consume();
                setToken(TokenType.NOT_EQUAL);
            }
        }

        return false;
    }

    /// Read the next character
    private void read() {
        // handle newline
        if (cur == '\n') {
            line++;
            col = 0;
        }
        try {
            int input = file.read();
            cur = (char) input;
            col++;
            if (input == -1) {
                eof = true;
            }
        } catch (IOException ex) {
            // do nothing for now
            eof = true;
        }
    }

    /// Insert the current character into the curStr
    /// and advanced the lexer
    private void consume() {
        curString.append(cur);
        read();
    }
}