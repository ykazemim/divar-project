package cli;

import config.Config;
import engine.QueryEngine;

import java.util.Set;

public class QueryExecutor {
    private final QueryEngine engine;
    private static final String SEPARATOR = "─────────────────────────────────────────────────────────────";
    
    public QueryExecutor(QueryEngine engine) {
        this.engine = engine;
    }
    
    public void execute(CommandParser.ParsedCommand command) {
        if (command == null) {
            return;
        }
        
        long startTime = System.currentTimeMillis();
        String[] args = command.getArguments();
        
        switch (command.getType()) {
            case FIND:
                handleFind(args[0]);
                break;
                
            case ADD_TAG:
                handleAddTag(args[0], args[1]);
                break;
                
            case REMOVE_TAG:
                handleRemoveTag(args[0], args[1]);
                break;
                
            case GET_USER_PROFILE:
                handleGetUserProfile(args[0]);
                break;
                
            case INVALID:
                printError("Invalid command format");
                break;
        }
        
        if (Config.isShowExecutionTime()) {
            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;
            System.out.println(String.format("⏱  Query executed in %.3f seconds", duration));
        }
        
        System.out.println(SEPARATOR);
    }
    
    private void handleFind(String category) {
        Set<String> users = engine.findByCategory(category);
        
        if (users.isEmpty()) {
            users = engine.findByTag(category);
        }
        
        int totalUsers = engine.getTotalUsers();
        double percentage = totalUsers > 0 ? (users.size() * 100.0 / totalUsers) : 0.0;
        
        System.out.println("┌─ FIND: " + category);
        System.out.println("└─ ");
        
        if (users.isEmpty()) {
            System.out.println("   (no users found)");
        } else {
            for (String userId : users) {
                System.out.println("   • " + userId);
            }
        }
        
        System.out.println();
        System.out.println("├─ Results: " + users.size() + " user(s) out of " + totalUsers);
        System.out.println("└─ Percentage: " + String.format("%.2f%%", percentage));
    }
    
    private void handleAddTag(String tag, String userId) {
        boolean success = engine.addTag(userId, tag);
        
        System.out.println("┌─ ADD_TAG");
        System.out.println("├─ User: " + userId);
        System.out.println("├─ Tag: " + tag);
        System.out.println("└─ ");
        
        if (success) {
            printSuccess("Tag '" + tag + "' added to " + userId);
        } else {
            printError("User " + userId + " not found");
        }
    }
    
    private void handleRemoveTag(String tag, String userId) {
        boolean success = engine.removeTag(userId, tag);
        
        System.out.println("┌─ REMOVE_TAG");
        System.out.println("├─ User: " + userId);
        System.out.println("├─ Tag: " + tag);
        System.out.println("└─ ");
        
        if (success) {
            printSuccess("Tag '" + tag + "' removed from " + userId);
        } else {
            printError("Tag '" + tag + "' not found on " + userId);
        }
    }
    
    private void handleGetUserProfile(String userId) {
        String profile = engine.getUserProfile(userId);
        
        System.out.println("┌─ GET_USER_PROFILE: " + userId);
        System.out.println("└─ ");
        
        if (profile != null) {
            System.out.println(profile);
        } else {
            printError("User " + userId + " not found");
        }
    }
    
    private void printSuccess(String message) {
        System.out.println("   ✓ " + message);
    }
    
    private void printError(String message) {
        System.out.println("   ✗ ERROR: " + message);
    }
}
