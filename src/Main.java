import cli.CommandParser;
import cli.QueryExecutor;
import engine.QueryEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final String DEFAULT_DATA_DIR = "data/profiles";

    public static void main(String[] args) {
        try {
            String dataDir = args.length > 0 ? args[0] : DEFAULT_DATA_DIR;
            QueryEngine engine = new QueryEngine();
            engine.loadProfiles(dataDir);

            CommandParser parser = new CommandParser();
            QueryExecutor executor = new QueryExecutor(engine);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;

            System.out.println("Enter your command: ");
            while ((line = reader.readLine()) != null) {
                CommandParser.ParsedCommand command = parser.parse(line);
                executor.execute(command);
                System.out.print("Enter your command: ");
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
