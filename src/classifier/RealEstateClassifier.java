package classifier;

import model.UserProfile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealEstateClassifier implements Classifier {
    
    private static final String[] EXPLICIT_REAL_ESTATE_KEYWORDS = {
        "مشاور.*املاک",
        "آژانس.*املاک",
        "بنگاه.*املاک",
        "املاک.*فروشی",
        "مشاور.*مسکن"
    };
    
    private static final String[] REAL_ESTATE_CATEGORIES = {
        "real-estate",
        "real_estate",
        "apartment-sell",
        "apartment-rent",
        "house-villa-sell",
        "house-villa-rent",
        "commercial-sell",
        "commercial-rent",
        "plot-old",
        "plot-project"
    };
    
    private static final String[] REAL_ESTATE_ACTIVITY_PATTERNS = {
        "بازار.*املاک",
        "فایل.*املاک",
        "ملک.*فروش",
        "آپارتمان.*فروش",
        "ویلا.*فروش",
        "سرمایه.*گذار.*املاک",
        "سرمایه.*گذاری.*ملک",
        "تهاتر.*ملک",
        "معاوضه.*ملک"
    };
    
    private static final String[] REAL_ESTATE_PROFESSIONAL_TERMS = {
        "متراژ",
        "سند.*تک.*برگ",
        "سند.*ششدانگ",
        "رهن.*اجاره",
        "پیش.*پرداخت",
        "ودیعه",
        "مسکونی",
        "تجاری"
    };
    
    private static final Pattern REAL_ESTATE_POSTS = Pattern.compile("cat[123]_slug[\"']?:\\s*[\"'][^\"']*(?:real-estate|apartment|house-villa|commercial|plot)[^\"']*[\"']");
    private static final Pattern MULTIPLE_REAL_ESTATE_POSTS = Pattern.compile("supply_total_posts[\"']?:\\s*([2-9]|[1-9][0-9]+)");
    private static final Pattern HIGH_REAL_ESTATE_SEARCHES = Pattern.compile("demand_searches_made[\"']?:\\s*([5-9][0-9]|[1-9][0-9]{2,})");
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        int realEstateScore = 0;
        
        for (String keyword : EXPLICIT_REAL_ESTATE_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                realEstateScore += 6;
                break;
            }
        }
        
        int categoryCount = 0;
        for (String category : REAL_ESTATE_CATEGORIES) {
            if (content.contains(category)) {
                categoryCount++;
            }
        }
        if (categoryCount >= 3) {
            realEstateScore += 4;
        } else if (categoryCount >= 2) {
            realEstateScore += 2;
        } else if (categoryCount == 1) {
            realEstateScore += 1;
        }
        
        int activityCount = 0;
        for (String pattern : REAL_ESTATE_ACTIVITY_PATTERNS) {
            if (content.matches("(?s).*" + pattern + ".*")) {
                activityCount++;
            }
        }
        if (activityCount >= 2) {
            realEstateScore += 3;
        } else if (activityCount == 1) {
            realEstateScore += 1;
        }
        
        int professionalTermCount = 0;
        for (String term : REAL_ESTATE_PROFESSIONAL_TERMS) {
            if (content.matches("(?s).*" + term + ".*")) {
                professionalTermCount++;
            }
        }
        if (professionalTermCount >= 4) {
            realEstateScore += 3;
        } else if (professionalTermCount >= 2) {
            realEstateScore += 1;
        }
        
        if (profile.hasCategory("businesses")) {
            realEstateScore += 3;
        }
        
        Matcher postMatcher = MULTIPLE_REAL_ESTATE_POSTS.matcher(content);
        if (postMatcher.find() && REAL_ESTATE_POSTS.matcher(content).find()) {
            int posts = Integer.parseInt(postMatcher.group(1));
            if (posts >= 3) {
                realEstateScore += 2;
            }
        }
        
        Matcher searchMatcher = HIGH_REAL_ESTATE_SEARCHES.matcher(content);
        if (searchMatcher.find() && content.matches("(?s).*apartment|real-estate.*")) {
            int searches = Integer.parseInt(searchMatcher.group(1));
            if (searches >= 100) {
                realEstateScore += 2;
            } else if (searches >= 50) {
                realEstateScore += 1;
            }
        }
        
        return realEstateScore >= 6;
    }
    
    @Override
    public String getCategoryName() {
        return "real_estate_agents";
    }
}
