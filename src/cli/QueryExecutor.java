package cli;

import engine.QueryEngine;

import java.util.Set;

public class QueryExecutor {
    private final QueryEngine engine;

    public QueryExecutor(QueryEngine engine) {
        this.engine = engine;
    }

    public void execute(CommandParser.ParsedCommand command) {
        if (command == null) {
            return;
        }

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
                System.out.println("ERROR: Invalid command format");
                break;
        }
    }

    private void handleFind(String category) {
        Set<String> users = engine.findByCategory(category);

        if (users.isEmpty()) {
            users = engine.findByTag(category);
        }

        for (String userId : users) {
            System.out.println(userId);
        }
    }

    private void handleAddTag(String tag, String userId) {
        boolean success = engine.addTag(userId, tag);

        if (success) {
            System.out.println("SUCCESS: Tag '" + tag + "' added to " + userId + ".");
        } else {
            System.out.println("ERROR: User " + userId + " not found.");
        }
    }

    private void handleRemoveTag(String tag, String userId) {
        boolean success = engine.removeTag(userId, tag);

        if (success) {
            System.out.println("SUCCESS: Tag '" + tag + "' removed from " + userId + ".");
        } else {
            System.out.println("ERROR: Tag '" + tag + "' not found on " + userId + ".");
        }
    }

    private void handleGetUserProfile(String userId) {
        String profile = engine.getUserProfile(userId);

        if (profile != null) {
            System.out.println(profile);
        } else {
            System.out.println("ERROR: User " + userId + " not found.");
        }
    }
}
