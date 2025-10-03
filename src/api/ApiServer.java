package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import engine.QueryEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApiServer {
    private final QueryEngine engine;
    private final int port;
    private HttpServer server;
    
    public ApiServer(QueryEngine engine, int port) {
        this.engine = engine;
        this.port = port;
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/users", new UsersHandler());
        server.createContext("/health", new HealthHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              API Server Started                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🌐 Server running at: http://localhost:" + port);
        System.out.println();
        System.out.println("Available endpoints:");
        System.out.println("  GET    /users?category=<category>  - Find users by category");
        System.out.println("  GET    /users/<userId>             - Get user profile");
        System.out.println("  POST   /users/<userId>/tags        - Add tag (body: {\"tag\":\"name\"})");
        System.out.println("  DELETE /users/<userId>/tags/<tag>  - Remove tag");
        System.out.println("  GET    /health                     - Health check");
        System.out.println();
        System.out.println("Press Ctrl+C to stop the server");
        System.out.println("─────────────────────────────────────────────────────────────");
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    
    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, JsonBuilder.error("Method not allowed"));
                return;
            }
            
            Map<String, Object> health = new HashMap<>();
            health.put("status", "ok");
            health.put("totalUsers", engine.getTotalUsers());
            
            sendResponse(exchange, 200, JsonBuilder.toJson(health));
        }
    }
    
    class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            
            String[] pathParts = path.split("/");
            
            if (pathParts.length == 2) {
                if ("GET".equals(method) && query != null) {
                    handleFindUsers(exchange, query);
                } else {
                    sendResponse(exchange, 400, JsonBuilder.error("Missing category parameter"));
                }
            } else if (pathParts.length == 3) {
                String userId = pathParts[2];
                if ("GET".equals(method)) {
                    handleGetUser(exchange, userId);
                } else {
                    sendResponse(exchange, 405, JsonBuilder.error("Method not allowed"));
                }
            } else if (pathParts.length == 4 && "tags".equals(pathParts[3])) {
                String userId = pathParts[2];
                if ("POST".equals(method)) {
                    handleAddTag(exchange, userId);
                } else {
                    sendResponse(exchange, 405, JsonBuilder.error("Method not allowed"));
                }
            } else if (pathParts.length == 5 && "tags".equals(pathParts[3])) {
                String userId = pathParts[2];
                String tag = URLDecoder.decode(pathParts[4], StandardCharsets.UTF_8);
                if ("DELETE".equals(method)) {
                    handleRemoveTag(exchange, userId, tag);
                } else {
                    sendResponse(exchange, 405, JsonBuilder.error("Method not allowed"));
                }
            } else {
                sendResponse(exchange, 404, JsonBuilder.error("Endpoint not found"));
            }
        }
        
        private void handleFindUsers(HttpExchange exchange, String query) throws IOException {
            Map<String, String> params = parseQuery(query);
            String category = params.get("category");
            
            if (category == null) {
                sendResponse(exchange, 400, JsonBuilder.error("Missing category parameter"));
                return;
            }
            
            Set<String> users = engine.findByCategory(category);
            if (users.isEmpty()) {
                users = engine.findByTag(category);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("count", users.size());
            response.put("total", engine.getTotalUsers());
            response.put("percentage", String.format("%.2f", users.size() * 100.0 / engine.getTotalUsers()));
            response.put("users", users);
            
            sendResponse(exchange, 200, JsonBuilder.toJson(response));
        }
        
        private void handleGetUser(HttpExchange exchange, String userId) throws IOException {
            String profile = engine.getUserProfile(userId);
            
            if (profile == null) {
                sendResponse(exchange, 404, JsonBuilder.error("User not found: " + userId));
                return;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("profile", profile);
            
            sendResponse(exchange, 200, JsonBuilder.toJson(response));
        }
        
        private void handleAddTag(HttpExchange exchange, String userId) throws IOException {
            String body = readRequestBody(exchange);
            String tag = extractTagFromJson(body);
            
            if (tag == null) {
                sendResponse(exchange, 400, JsonBuilder.error("Invalid request body. Expected: {\"tag\":\"tagname\"}"));
                return;
            }
            
            boolean success = engine.addTag(userId, tag);
            
            if (success) {
                sendResponse(exchange, 200, JsonBuilder.success("Tag '" + tag + "' added to " + userId));
            } else {
                sendResponse(exchange, 404, JsonBuilder.error("User not found: " + userId));
            }
        }
        
        private void handleRemoveTag(HttpExchange exchange, String userId, String tag) throws IOException {
            boolean success = engine.removeTag(userId, tag);
            
            if (success) {
                sendResponse(exchange, 200, JsonBuilder.success("Tag '" + tag + "' removed from " + userId));
            } else {
                sendResponse(exchange, 404, JsonBuilder.error("Tag '" + tag + "' not found on user " + userId));
            }
        }
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                result.put(
                    URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                );
            }
        }
        return result;
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    private String extractTagFromJson(String json) {
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            int tagStart = json.indexOf("\"tag\"");
            if (tagStart != -1) {
                int colonPos = json.indexOf(":", tagStart);
                if (colonPos != -1) {
                    int valueStart = json.indexOf("\"", colonPos);
                    if (valueStart != -1) {
                        int valueEnd = json.indexOf("\"", valueStart + 1);
                        if (valueEnd != -1) {
                            return json.substring(valueStart + 1, valueEnd);
                        }
                    }
                }
            }
        }
        return null;
    }
}
