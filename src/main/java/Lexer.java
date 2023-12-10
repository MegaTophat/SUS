import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Lexer for SUS
 */
public class Lexer {
    // Stream of characters for us to lex
    private final InputStream inputStream;
    // Our working token builder storage
    private final StringBuilder currentTokenString;

    // Character currently being matched to something
    private char currentCharacter;

    // Lexeme storage variable
    private Lexeme currentLexeme;

    // Line, column, and storage variables
    private int lineNum;
    private int columnNum;
    private int startLine;
    private int startCol;
    private boolean reachedEndOfFile;

    // Set up the infrastructure needed to begin lexing a new set of tokens
    public Lexer(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.currentTokenString = new StringBuilder();
        this.lineNum = 1;
        this.columnNum = 0;
        this.reachedEndOfFile = false;

        this.readNextLiteralCharacter();
    }

    public static void main(final String[] args) throws FileNotFoundException {
        final InputStream inputStream;

        if (args.length > 0) {
            final String workingDirectory = System.getProperty("user.dir");
            final File file = new File(workingDirectory, args[0]);

            inputStream = new FileInputStream(file);
        } else {
            inputStream = System.in;
        }

        Lexer lex = new Lexer(inputStream);
        Lexeme token;

        do {
            token = lex.nextLexeme();
            System.out.println(token);
        } while (token.tokenType() != TokenType.EOF);
    }

    private void setToken(final TokenType token) {
        this.currentLexeme = new Lexeme(token, this.currentTokenString.toString(), this.startLine, this.startCol);
    }

    // skip extraneous whitespace
    private void skipIgnoredCharacters() {
        // skip all the blank characters
        while (this.currentCharacter != '\n' && (Character.isSpaceChar(this.currentCharacter))) {
            this.readNextLiteralCharacter();
        }
    }

    Lexeme nextLexeme() {
        // start the matching
        this.skipIgnoredCharacters();
        this.currentTokenString.delete(0, this.currentTokenString.length());
        this.startLine = this.lineNum;
        this.startCol = this.columnNum;

        // handle eof
        if (this.reachedEndOfFile) {
            this.setToken(TokenType.EOF);
            return this.currentLexeme();
        }

        if (this.matchSingleCharacterToken())
            return this.currentLexeme();
        else if (this.matchNumber())
            return this.currentLexeme();
        else if (this.matchWord())
            return this.currentLexeme();
        else if (this.matchFixed())
            return this.currentLexeme();
        else if (this.matchString()) {
            return this.currentLexeme();
        } else
            this.consumeCurrentCharacter();

        // invalid
        this.setToken(TokenType.UNKNOWN);
        return this.currentLexeme();
    }

    Lexeme currentLexeme() {
        return this.currentLexeme;
    }

    // Attempt to match tokens which are a single character
    public boolean matchSingleCharacterToken() {
        final TokenType token = switch (this.currentCharacter) {
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '*' -> TokenType.TIMES;
            case '/' -> TokenType.DIVIDE;
            case '^' -> TokenType.POW;
            case '`' -> TokenType.ARRAY_BOUNDARY;
            case '®' -> TokenType.REMAINDER;
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case '\n' -> TokenType.NEWLINE;
            case '=' -> TokenType.EQUAL;
            case '.' -> TokenType.DOT;
            default -> TokenType.UNKNOWN;
        };

        // did not match
        if (token == TokenType.UNKNOWN) {
            return false;
        }

        // one of the single character tokens matched, consume it
        this.consumeCurrentCharacter();
        this.setToken(token);

        return true;
    }

    // Match an integer
    private void consumeInteger() {
        while (Character.isDigit(this.currentCharacter)) {
            this.consumeCurrentCharacter();
        }
    }

    // consume characters until we hit the other " marker, the end of the line, or the end of the file
    private void consumeString() {
        while (this.currentCharacter != '"' && this.currentCharacter != '\n' && !this.reachedEndOfFile) {
            this.consumeCurrentCharacter();
        }
    }

    private boolean matchString() {
        if (this.currentCharacter != '"') {
            return false;
        }

        // consume the beginning " mark
        this.consumeCurrentCharacter();
        this.consumeString();

        if (this.currentCharacter != '"') {
            this.setToken(TokenType.UNKNOWN);
            return false;
        }

        // consume the ending " symbol
        this.consumeCurrentCharacter();
        this.setToken(TokenType.STRING);
        return true;
    }

    /// attempt to match a number
    private boolean matchNumber() {
        if (!Character.isDigit(this.currentCharacter))
            return false;

        // consume the beginning part of the number, potentially the entire number
        this.consumeInteger();

        // is there a dot?
        if (this.currentCharacter != '.') {
            this.setToken(TokenType.NUMBER);
            return true;
        }

        // consume the singular dot
        this.consumeCurrentCharacter();

        if (Character.isDigit(this.currentCharacter)) {
            // consume the ending part of the number
            this.consumeInteger();
            this.setToken(TokenType.NUMBER);
        } else {
            // an integer followed by a dot and not another integer isn't a number
            this.setToken(TokenType.UNKNOWN);
        }

        return true;
    }

    private boolean matchWord() {
        if (!Character.isAlphabetic(this.currentCharacter))
            return false;

        // consume letters
        while (Character.isAlphabetic(this.currentCharacter)) {
            this.consumeCurrentCharacter();
        }

        // grab the character sequence we just consumed for matching
        final String str = this.currentTokenString.toString();

        switch (str) {
            case "PRINT" -> this.setToken(TokenType.PRINT);
            case "READ" -> this.setToken(TokenType.READ);
            case "sus" -> this.setToken(TokenType.SUS);
            case "swotus" -> this.setToken(TokenType.SWOTUS);
            case "swaus" -> this.setToken(TokenType.SWAUS);
            case "shower" -> this.setToken(this.matchComment());
            case "susmoney" -> this.setToken(TokenType.SUSMONEY);
            case "ARRAY" -> this.setToken(TokenType.ARRAY);
            default ->
                // alphabetical word which isn't a susword, we matched a valid variable name
                    this.setToken(TokenType.VARIABLE_NAME);
        }

        return true;
    }

    private TokenType matchComment() {
        // consume until the end of the line, or we reach EOF
        while (this.currentCharacter != '\n' && !this.reachedEndOfFile) {
            this.consumeCurrentCharacter();
        }

        final String currentTokenString = this.currentTokenString.toString();

        if (currentTokenString.startsWith("shower thought") && currentTokenString.endsWith("sandwich")) {
            // This is a syntactically correct comment
            return TokenType.COMMENT;
        }

        return TokenType.UNKNOWN;
    }

    // Match a multi-character fixed width token
    private boolean matchFixed() {
        if (this.currentCharacter == '<') {
            this.consumeCurrentCharacter();
            this.setToken(TokenType.LESS_THAN);

            // check for the second part
            if (this.currentCharacter == '=') {
                this.consumeCurrentCharacter();
                this.setToken(TokenType.LESS_THAN_OR_EQUAL);
            }

            return true;
        } else if (this.currentCharacter == '>') {
            this.consumeCurrentCharacter();
            this.setToken(TokenType.GREATER_THAN);
            if (this.currentCharacter == '=') {
                this.consumeCurrentCharacter();
                this.setToken(TokenType.GREATER_THAN_OR_EQUAL);
            }

            return true;
        } else if (this.currentCharacter == '!') {
            this.consumeCurrentCharacter();

            if (this.currentCharacter == '=') {
                this.consumeCurrentCharacter();
                this.setToken(TokenType.NOT_EQUAL);
            }
        }

        return false;
    }

    // Read the next character
    private void readNextLiteralCharacter() {
        // handle newline
        if (this.currentCharacter == '\n') {
            ++this.lineNum;
            this.columnNum = 0;
        }

        final int byteRead;

        try {
            byteRead = this.inputStream.read();
        } catch (final IOException ex) {
            throw new UncheckedIOException("I/O Exception while advancing through input stream!", ex);
        }

        this.currentCharacter = (char) byteRead;
        this.columnNum++;

        if (byteRead == -1) {
            this.reachedEndOfFile = true;
        }
    }

    // Append our current character to the build-in-progress string and read the next character
    private void consumeCurrentCharacter() {
        this.currentTokenString.append(this.currentCharacter);

        this.readNextLiteralCharacter();
    }
}