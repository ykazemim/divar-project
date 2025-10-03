package classifier;

import model.UserProfile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessClassifier implements Classifier {
    
    private static final String[] BUSINESS_IDENTITY_KEYWORDS = {
        "business_type[\"']?:\\s*[\"']business",
        "business_type[\"']?:\\s*[\"']premium-panel",
        "BUSINESS_[0-9]+",
        "آژانس",
        "بنگاه",
        "شرکت"
    };
    
    private static final String[] PROFESSIONAL_SERVICE_KEYWORDS = {
        "مشاور",
        "مشاوره",
        "خدمات.*حرفه",
        "سرویس.*دهنده",
        "ارائه.*دهنده",
        "باربری",
        "حمل.*نقل",
        "حمل.*بار",
        "نقل.*مکان"
    };
    
    private static final String[] INTERMEDIARY_KEYWORDS = {
        "واسطه",
        "واسط",
        "دلال",
        "دلالی",
        "کمیسیون",
        "سرنخ",
        "Lead.*Generation",
        "تولید.*سرنخ",
        "فایل.*املاک"
    };
    
    private static final String[] BUSINESS_BEHAVIOR_PATTERNS = {
        "سابقه.*کار",
        "سال.*سابقه",
        "تخصص.*در",
        "متخصص.*در",
        "حرفه.*ای",
        "ارائه.*خدمات"
    };
    
    private static final Pattern HIGH_POST_COUNT = Pattern.compile("supply_total_posts[\"']?:\\s*([5-9]|[1-9][0-9]+)");
    private static final Pattern VERY_HIGH_POST_COUNT = Pattern.compile("supply_total_posts[\"']?:\\s*(1[0-9]|[2-9][0-9]|[1-9][0-9]{2,})");
    private static final Pattern HIGH_REVENUE = Pattern.compile("supply_total_revenue[\"']?:\\s*([1-9][0-9]{6,})");
    private static final Pattern VERY_HIGH_REVENUE = Pattern.compile("supply_total_revenue[\"']?:\\s*([5-9][0-9]{6,}|[1-9][0-9]{7,})");
    private static final Pattern HIGH_VIEWS = Pattern.compile("supply_total_views[\"']?:\\s*([5-9][0-9]{2,}|[1-9][0-9]{3,})");
    private static final Pattern MULTIPLE_CATEGORIES = Pattern.compile("supply_unique_categories[\"']?:\\s*([3-9]|[1-9][0-9]+)");
    private static final Pattern HIGH_DEMAND_SEARCHES = Pattern.compile("demand_searches_made[\"']?:\\s*([1-9][0-9]{2,})");
    private static final Pattern VERY_HIGH_DEMAND_SEARCHES = Pattern.compile("demand_searches_made[\"']?:\\s*([2-9][0-9]{2,}|[1-9][0-9]{3,})");
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        int businessScore = 0;
        
        for (String keyword : BUSINESS_IDENTITY_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                businessScore += 5;
                break;
            }
        }
        
        int serviceKeywordCount = 0;
        for (String keyword : PROFESSIONAL_SERVICE_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                serviceKeywordCount++;
            }
        }
        if (serviceKeywordCount >= 3) {
            businessScore += 4;
        } else if (serviceKeywordCount >= 2) {
            businessScore += 2;
        }
        
        for (String keyword : INTERMEDIARY_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                businessScore += 3;
                break;
            }
        }
        
        int behaviorCount = 0;
        for (String pattern : BUSINESS_BEHAVIOR_PATTERNS) {
            if (content.matches("(?s).*" + pattern + ".*")) {
                behaviorCount++;
            }
        }
        if (behaviorCount >= 3) {
            businessScore += 3;
        } else if (behaviorCount >= 2) {
            businessScore += 1;
        }
        
        Matcher postMatcher = VERY_HIGH_POST_COUNT.matcher(content);
        if (postMatcher.find()) {
            int posts = Integer.parseInt(postMatcher.group(1));
            if (posts >= 20) {
                businessScore += 5;
            } else if (posts >= 10) {
                businessScore += 3;
            }
        } else {
            postMatcher = HIGH_POST_COUNT.matcher(content);
            if (postMatcher.find()) {
                int posts = Integer.parseInt(postMatcher.group(1));
                if (posts >= 7) {
                    businessScore += 2;
                }
            }
        }
        
        if (VERY_HIGH_REVENUE.matcher(content).find()) {
            businessScore += 4;
        } else if (HIGH_REVENUE.matcher(content).find()) {
            businessScore += 2;
        }
        
        if (HIGH_VIEWS.matcher(content).find()) {
            businessScore += 1;
        }
        
        if (MULTIPLE_CATEGORIES.matcher(content).find()) {
            businessScore += 2;
        }
        
        Matcher demandMatcher = VERY_HIGH_DEMAND_SEARCHES.matcher(content);
        if (demandMatcher.find()) {
            int searches = Integer.parseInt(demandMatcher.group(1));
            if (searches >= 200) {
                businessScore += 3;
            } else {
                businessScore += 1;
            }
        }
        
        return businessScore >= 8;
    }
    
    @Override
    public String getCategoryName() {
        return "businesses";
    }
}
