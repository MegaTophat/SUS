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
    private int currentLexemeIndex;
    private final Map<String, Integer> variables;

    public Sus(String sourceCode) {
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
    }
}
