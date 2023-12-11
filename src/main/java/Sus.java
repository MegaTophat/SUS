import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Sus {
    public static void repl() {
        Scanner scan = new Scanner(System.in);
        ReferenceEnvironment env = new ReferenceEnvironment();

        while (true) {
            System.out.print("> ");
            System.out.flush();
            String line = scan.nextLine() + "\n";
            Lexer lex = new Lexer(new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)));
            Parser parser = new Parser(lex);
            ParseTree program = parser.parse();
            EvaluationResult result = program.evaluate(env);

            if (result != null) {
                System.out.println(result.asNumber());
            }
        }
    }

    public static void runFile(String fileName) {
        try {
            InputStream file = new FileInputStream(fileName);
            ReferenceEnvironment env = new ReferenceEnvironment();
            Lexer lex = new Lexer(file);
            Parser parser = new Parser(lex);

            ParseTree program = parser.parse();
            program.evaluate(env);
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + fileName);
        }

    }


    public static void main(String[] args) {
        // do a repl if args is empty
        if (args.length == 0) {
            repl();
        } else {
            runFile(args[0]);
        }
    }
}
