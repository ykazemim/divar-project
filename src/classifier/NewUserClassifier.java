package classifier;

import model.UserProfile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserClassifier implements Classifier {
    private static final Pattern POSTS_PATTERN = Pattern.compile("supply_total_posts[\"']?:\\s*([0-9]+)");
    private static final Pattern SEARCHES_PATTERN = Pattern.compile("demand_searches_made[\"']?:\\s*([0-9]+)");
    private static final int NEW_USER_THRESHOLD = 5;
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        
        int totalPosts = extractMetric(content, POSTS_PATTERN);
        int totalSearches = extractMetric(content, SEARCHES_PATTERN);
        
        return (totalPosts + totalSearches) <= NEW_USER_THRESHOLD;
    }
    
    private int extractMetric(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    @Override
    public String getCategoryName() {
        return "new_users";
    }
}
