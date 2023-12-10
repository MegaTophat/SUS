import java.io.*;
import java.nio.charset.StandardCharsets;
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
    public static void repl(){
        Scanner scan = new Scanner(System.in);
        RefEnv env = new RefEnv();

        while(true) {
            System.out.print("> ");
            System.out.flush();
            String line = scan.nextLine() + "\n";
            Lexer lex = new Lexer(new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)));
            Parser parser = new Parser(lex);
            ParseTree program = parser.parse();
            EvalResult result = program.eval(env);

            if(result != null) {
                System.out.println(result.asReal());
            }
        }
    }

    public static void runFile(String fileName) {
        try {
            InputStream file = new FileInputStream(fileName);
            RefEnv env = new RefEnv();
            Lexer lex = new Lexer(file);
            Parser parser = new Parser(lex);

            ParseTree program = parser.parse();
            program.eval(env);
        } catch(FileNotFoundException ex) {
            System.err.println("File not found: " + fileName);
        }

    }


    public static void main(String [] args ) {
        // do a repl if args is empty
        if(args.length == 0) {
            repl();
        } else {
            runFile(args[0]);
        }
    }
}
