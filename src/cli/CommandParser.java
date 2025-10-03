package cli;

public class CommandParser {
    
    public ParsedCommand parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = input.trim();
        String[] parts = trimmed.split("\\s+");
        
        if (parts.length < 2) {
            return new ParsedCommand(CommandType.INVALID, parts);
        }
        
        String command = parts[0];
        
        if (command.equals("FIND") && parts.length >= 2) {
            String category = parts[1];
            return new ParsedCommand(CommandType.FIND, new String[]{category});
        }
        
        if (command.equals("GET_USER_PROFILE") && parts.length >= 2) {
            String userId = parts[1];
            return new ParsedCommand(CommandType.GET_USER_PROFILE, new String[]{userId});
        }
        
        if (command.equals("ADD_TAG") && parts.length >= 3) {
            String userId = parts[1];
            String tag = parts[2];
            return new ParsedCommand(CommandType.ADD_TAG, new String[]{tag, userId});
        }
        
        if (command.equals("REMOVE_TAG") && parts.length >= 3) {
            String userId = parts[1];
            String tag = parts[2];
            return new ParsedCommand(CommandType.REMOVE_TAG, new String[]{tag, userId});
        }
        
        return new ParsedCommand(CommandType.INVALID, parts);
    }
    
    public static class ParsedCommand {
        private final CommandType type;
        private final String[] arguments;
        
        public ParsedCommand(CommandType type, String[] arguments) {
            this.type = type;
            this.arguments = arguments;
        }
        
        public CommandType getType() {
            return type;
        }
        
        public String[] getArguments() {
            return arguments;
        }
    }
    
    public enum CommandType {
        FIND,
        ADD_TAG,
        REMOVE_TAG,
        GET_USER_PROFILE,
        INVALID
    }
}
