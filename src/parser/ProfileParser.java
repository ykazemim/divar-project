package parser;

import model.UserProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileParser {
    private static final Pattern UUID_PATTERN = Pattern.compile("# Final Profile \\(UUID\\): ([a-f0-9-]+)");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("user_(\\d+)\\.md");
    
    public UserProfile parse(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        String fileName = filePath.getFileName().toString();
        
        String userId = extractUserId(fileName);
        String uuid = extractUuid(content);
        
        return new UserProfile(userId, uuid, content);
    }
    
    private String extractUserId(String fileName) {
        Matcher matcher = USER_ID_PATTERN.matcher(fileName);
        if (matcher.find()) {
            return "user_" + matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid filename format: " + fileName);
    }
    
    private String extractUuid(String content) {
        Matcher matcher = UUID_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }
}
