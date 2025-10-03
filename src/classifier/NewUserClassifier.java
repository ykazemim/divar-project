package classifier;

import model.UserProfile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserClassifier implements Classifier {
    
    private static final Pattern POSTS_PATTERN = Pattern.compile("supply_total_posts[\"']?:\\s*([0-9]+)");
    private static final Pattern SEARCHES_PATTERN = Pattern.compile("demand_searches_made[\"']?:\\s*([0-9]+)");
    private static final Pattern CONTACTED_PATTERN = Pattern.compile("demand_posts_contacted[\"']?:\\s*([0-9]+)");
    private static final Pattern CHATS_PATTERN = Pattern.compile("supply_total_chats[\"']?:\\s*([0-9]+)");
    private static final Pattern POSTS_VIEWED_PATTERN = Pattern.compile("demand_posts_viewed[\"']?:\\s*([0-9]+)");
    private static final Pattern PUBLISHED_POSTS_PATTERN = Pattern.compile("supply_published_posts[\"']?:\\s*([0-9]+)");
    private static final Pattern CALLS_PATTERN = Pattern.compile("supply_total_calls[\"']?:\\s*([0-9]+)");
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        
        int totalPosts = extractMetric(content, POSTS_PATTERN);
        int publishedPosts = extractMetric(content, PUBLISHED_POSTS_PATTERN);
        int totalSearches = extractMetric(content, SEARCHES_PATTERN);
        int totalContacted = extractMetric(content, CONTACTED_PATTERN);
        int totalChats = extractMetric(content, CHATS_PATTERN);
        int totalCalls = extractMetric(content, CALLS_PATTERN);
        int postsViewed = extractMetric(content, POSTS_VIEWED_PATTERN);
        
        int totalInteractions = totalContacted + totalChats + totalCalls;
        int totalActivity = totalPosts + totalSearches + postsViewed;
        
        if (totalActivity == 0 && totalInteractions == 0) {
            return true;
        }
        
        if (totalPosts == 0 && publishedPosts == 0 && totalSearches == 0 && postsViewed == 0) {
            return true;
        }
        
        if (totalPosts == 0 && totalSearches == 0 && totalInteractions == 0) {
            return true;
        }
        
        return false;
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
