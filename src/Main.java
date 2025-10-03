import cli.CommandParser;
import cli.QueryExecutor;
import config.Config;
import engine.QueryEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final String DEFAULT_DATA_DIR = "data/profiles";
    private static final String BANNER = 
        "╔═══════════════════════════════════════════════════════════╗\n" +
        "║         Divar User Analysis - Query Engine v1.0           ║\n" +
        "╚═══════════════════════════════════════════════════════════╝";
    
    public static void main(String[] args) {
        try {
            System.out.println(BANNER);
            System.out.println();
            
            String dataDir = args.length > 0 ? args[0] : DEFAULT_DATA_DIR;
            
            System.out.println("📂 Loading profiles from: " + dataDir);
            long startTime = System.currentTimeMillis();
            
            QueryEngine engine = new QueryEngine();
            int profileCount = engine.loadProfiles(dataDir);
            
            long endTime = System.currentTimeMillis();
            double loadTime = (endTime - startTime) / 1000.0;
            
            System.out.println("✓ Loaded " + profileCount + " profiles");
            if (Config.isShowExecutionTime()) {
                System.out.println(String.format("⏱  Loaded in %.3f seconds", loadTime));
            }
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.println();
            
            CommandParser parser = new CommandParser();
            QueryExecutor executor = new QueryExecutor(engine);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            
            System.out.println("Ready for queries. Available commands:");
            System.out.println("   • FIND <category>");
            System.out.println("   • ADD_TAG <userId> <tag>");
            System.out.println("   • REMOVE_TAG <userId> <tag>");
            System.out.println("   • GET_USER_PROFILE <userId>");
            System.out.println("   • toggle_time (enable/disable execution time)");
            System.out.println("   • exit / quit");
            System.out.println();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty()) {
                    continue;
                }
                
                if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                    System.out.println("\nBye!");
                    break;
                }
                
                if (line.equalsIgnoreCase("toggle_time")) {
                    Config.toggleExecutionTime();
                    System.out.println("⏱  Execution time display: " + 
                        (Config.isShowExecutionTime() ? "✓ ON" : "✗ OFF"));
                    System.out.println("─────────────────────────────────────────────────────────────");
                    System.out.println();
                    continue;
                }
                
                System.out.println();
                CommandParser.ParsedCommand command = parser.parse(line);
                executor.execute(command);
                System.out.println();
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
