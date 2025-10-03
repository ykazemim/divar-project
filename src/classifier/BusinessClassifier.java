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
        "فروشگاه",
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
    private static final Pattern HIGH_REVENUE = Pattern.compile("supply_total_revenue[\"']?:\\s*([1-9][0-9]{6,})");
    private static final Pattern HIGH_VIEWS = Pattern.compile("supply_total_views[\"']?:\\s*([5-9][0-9]{2,}|[1-9][0-9]{3,})");
    private static final Pattern MULTIPLE_CATEGORIES = Pattern.compile("supply_unique_categories[\"']?:\\s*([2-9]|[1-9][0-9]+)");
    private static final Pattern HIGH_DEMAND_SEARCHES = Pattern.compile("demand_searches_made[\"']?:\\s*([1-9][0-9]{2,})");
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        int businessScore = 0;
        
        for (String keyword : BUSINESS_IDENTITY_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                businessScore += 4;
                break;
            }
        }
        
        int serviceKeywordCount = 0;
        for (String keyword : PROFESSIONAL_SERVICE_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                serviceKeywordCount++;
            }
        }
        if (serviceKeywordCount >= 2) {
            businessScore += 3;
        } else if (serviceKeywordCount == 1) {
            businessScore += 1;
        }
        
        for (String keyword : INTERMEDIARY_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                businessScore += 2;
                break;
            }
        }
        
        int behaviorCount = 0;
        for (String pattern : BUSINESS_BEHAVIOR_PATTERNS) {
            if (content.matches("(?s).*" + pattern + ".*")) {
                behaviorCount++;
            }
        }
        if (behaviorCount >= 2) {
            businessScore += 2;
        }
        
        Matcher postMatcher = HIGH_POST_COUNT.matcher(content);
        if (postMatcher.find()) {
            int posts = Integer.parseInt(postMatcher.group(1));
            if (posts >= 10) {
                businessScore += 4;
            } else if (posts >= 5) {
                businessScore += 2;
            }
        }
        
        if (HIGH_REVENUE.matcher(content).find()) {
            businessScore += 3;
        }
        
        if (HIGH_VIEWS.matcher(content).find()) {
            businessScore += 1;
        }
        
        if (MULTIPLE_CATEGORIES.matcher(content).find()) {
            businessScore += 2;
        }
        
        Matcher demandMatcher = HIGH_DEMAND_SEARCHES.matcher(content);
        if (demandMatcher.find()) {
            int searches = Integer.parseInt(demandMatcher.group(1));
            if (searches >= 100) {
                businessScore += 2;
            }
        }
        
        return businessScore >= 5;
    }
    
    @Override
    public String getCategoryName() {
        return "businesses";
    }
}
