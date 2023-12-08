import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sus {
    private final List<Lexeme> lexemes;
    private int currentLexemeIndex;
    private final Map<String, Integer> variables;

    public Sus(String sourceCode) {
        this.lexemes = lexer(sourceCode);
        this.currentLexemeIndex = 0;
        this.variables = new HashMap<>();
    }
//hello
    public static void main(String[] args) throws IOException {
        final String sourceCode;

        if (args.length == 0) {
            // Read filename from the user
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the filename: ");
            String filename = scanner.nextLine();

            sourceCode = Files.readString(Path.of(filename));
            scanner.close();
        } else {
            final String fileName = args[0];

            sourceCode = Files.readString(Path.of(fileName));
        }

        Sus interpreter = new Sus(sourceCode);
        interpreter.parseAndInterpret();
    }

    private List<Lexeme> lexer(String sourceCode) {
        List<Lexeme> lexemes = new ArrayList<>();

        // Split the source code into individual lexemes
        String[] tokens = sourceCode.split("\\s+");

        for (String token : tokens) {
            TokenType type = determineTokenType(token);
            lexemes.add(new Lexeme(token, type));
        }

        return lexemes;
    }

    private TokenType determineTokenType(String token) {
        // Logic to determine the token type goes here
        final Pattern numberPattern = Pattern.compile("\\d+");
        final Matcher numberMatcher = numberPattern.matcher(token);
        if (numberMatcher.matches()) {
            System.out.println("We fucking parsed a number!");
        }

        switch (token) {
            case "+":
                return TokenType.PLUS;
            case "-":
                return TokenType.MINUS;
            case "*":
                return TokenType.TIMES;
            case "/":
                return TokenType.DIVIDE;
            case "=":
                return TokenType.EQUAL;
            case "(":
                return TokenType.LPAREN;
            case ")":
                return TokenType.RPAREN;
            case "\"[^\"]*\"":
                return TokenType.STRING;
            case "PRINT":
                return TokenType.PRINT;
            case "sus":
                return TokenType.SUS;
            case "SWOTUS":
                return TokenType.SWOTUS;
            case "<=":
                return TokenType.LESS_THAN_OR_EQUAL;
            case ">=":
                return TokenType.GREATER_THAN_OR_EQUAL;
            case "<":
                return TokenType.LESS_THAN;
            case ">":
                return TokenType.GREATER_THAN;
            case "\\d+":
                return TokenType.NUMBER;
            default:
                return TokenType.UNKNOWN;
        }
    }

    private String getNextToken() {
        if (currentLexemeIndex < lexemes.size()) {
            return lexemes.get(currentLexemeIndex++).token();
        }
        return null;
    }

    private int parseExpression() {
        int result = parseTerm();

        while (currentLexemeIndex < lexemes.size()) {
            Lexeme operatorLexeme = getNextLexeme();
            String operator = operatorLexeme.token();

            if (operator.equals("+")) {
                result += parseTerm();
            } else if (operator.equals("-")) {
                result -= parseTerm();
            } else {
                currentLexemeIndex--; // Put back the non-operator token
                break;
            }
        }

        return result;
    }

    private int parseTerm() {
        int result = parseFactor();

        while (currentLexemeIndex < lexemes.size()) {
            Lexeme operatorLexeme = getNextLexeme();
            String operator = operatorLexeme.token();

            if (operator.equals("*")) {
                result *= parseFactor();
            } else if (operator.equals("/")) {
                int divisor = parseFactor();
                if (divisor != 0) {
                    result /= divisor;
                } else {
                    throw new RuntimeException("Division by zero");
                }
            } else {
                currentLexemeIndex--; // Put back the non-operator token
                break;
            }
        }

        return result;
    }

    private int parseFactor() {
        Lexeme currentLexeme = getNextLexeme();

        if (currentLexeme.type() == TokenType.NUMBER) {
            // Integer literal
            return Integer.parseInt(currentLexeme.token());
        } else if (currentLexeme.type() == TokenType.LPAREN) {
            // Handle parentheses for grouping
            int result = parseExpression();
            Lexeme nextLexeme = getNextLexeme();
            if (nextLexeme != null && nextLexeme.type() == TokenType.RPAREN) {
                return result;
            } else {
                throw new RuntimeException("Mismatched parentheses");
            }
        } else {
            System.out.println(this.determineTokenType(currentLexeme.token()));
            throw new RuntimeException("Unexpected token in factor: " + currentLexeme.token());
        }
    }



    private Lexeme getNextLexeme() {
        if (currentLexemeIndex < lexemes.size()) {
            return lexemes.get(currentLexemeIndex++);
        }
        return null;
    }

    private void parseAndInterpret() {
        while (currentLexemeIndex < lexemes.size()) {
            Lexeme currentLexeme = getNextLexeme();

            if (currentLexeme.type() == TokenType.NUMBER) {
                // Handle integer literals
                System.out.println("" + currentLexeme.token());
            } else if (currentLexeme.type() == TokenType.PRINT) {
                // Handle print statement
                Lexeme toPrintLexeme = getNextLexeme();
                String toPrint = toPrintLexeme.token();
                if (toPrint != null) {
                    if (toPrint.matches("\\d+")) {
                        // Print integer literals
                        System.out.println("" + Integer.parseInt(toPrint));
                    } else if (variables.containsKey(toPrint)) {
                        // Print variable values
                        System.out.println("" + variables.get(toPrint));
                    } else {
                        throw new RuntimeException("Invalid PRINT statement");
                    }
                } else {
                    throw new RuntimeException("Unexpected end of input");
                }
            } else if (currentLexeme.type() == TokenType.SUS) {
                // Handle 'sus' statement (if statement)
                Lexeme conditionVarLexeme = getNextLexeme();
                String conditionVar = conditionVarLexeme.token();
                Lexeme conditionOperatorLexeme = getNextLexeme();
                String conditionOperator = conditionOperatorLexeme.token();
                Lexeme conditionValueLexeme = getNextLexeme();
                int conditionValue = Integer.parseInt(conditionValueLexeme.token());

                int variableValue = variables.getOrDefault(conditionVar, 0);

                System.out.println(conditionVar);
                System.out.println(conditionOperator);

                boolean conditionResult = false;
                if (conditionOperator.equals("==")) {
                    conditionResult = (variableValue == conditionValue);
                } else if (conditionOperator.equals("<")) {
                    conditionResult = (variableValue < conditionValue);
                } else if (conditionOperator.equals(">")) {
                    conditionResult = (variableValue > conditionValue);
                } else if (conditionOperator.equals("<=")) {
                    conditionResult = (variableValue <= conditionValue);
                } else if (conditionOperator.equals(">=")) {
                    conditionResult = (variableValue >= conditionValue);
                }

                if (conditionResult) {
                    // Condition is true, execute the block
                    while (!currentLexeme.token().equals("SWOTUS")) {
                        parseAndInterpret(); // Execute the block inside 'sus'
                    }
                } else {
                    // Skip the block inside 'sus' as the condition is false
                    while (!currentLexeme.token().equals("SWOTUS")) {
                        getNextLexeme(); // Skip lexemes until 'SWOTUS'
                    }
                }
            } else {
                throw new RuntimeException("Invalid token: " + currentLexeme.token());
            }
        }
    }

    private record Lexeme(String token, TokenType type) {
    }
}
