import api.ApiServer;
import engine.QueryEngine;

import java.io.IOException;

public class Main {
    private static final String DEFAULT_DATA_DIR = "data/profiles";
    private static final int DEFAULT_PORT = 8080;
    
    public static void main(String[] args) {
        try {
            String dataDir = args.length > 0 ? args[0] : DEFAULT_DATA_DIR;
            int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
            
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║      Divar User Analysis - API Server v1.0               ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println();
            
            System.out.println("📂 Loading profiles from: " + dataDir);
            long startTime = System.currentTimeMillis();
            
            QueryEngine engine = new QueryEngine();
            int profileCount = engine.loadProfiles(dataDir);
            
            long endTime = System.currentTimeMillis();
            double loadTime = (endTime - startTime) / 1000.0;
            
            System.out.println("✓ Loaded " + profileCount + " profiles");
            System.out.println(String.format("⏱  Loaded in %.3f seconds", loadTime));
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.println();
            
            ApiServer server = new ApiServer(engine, port);
            server.start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\nShutting down server...");
                server.stop();
                System.out.println("Server stopped.");
            }));
            
            Thread.currentThread().join();
            
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("\nServer interrupted.");
        }
    }
}
